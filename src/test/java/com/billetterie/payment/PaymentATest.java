package com.billetterie.payment;

import com.billetterie.payment.infrastructure.controller.PaymentController;
import com.billetterie.payment.infrastructure.controller.dto.CartDto;
import com.billetterie.payment.infrastructure.controller.dto.CreditCardDto;
import com.billetterie.payment.infrastructure.controller.dto.PaymentDto;
import com.billetterie.payment.infrastructure.controller.dto.PaymentResultDto;
import com.billetterie.payment.infrastructure.service.EmailCustomerSupport;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Etantdonné;
import io.cucumber.java.fr.Etque;
import io.cucumber.java.fr.Quand;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

@AutoConfigureCache
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
@CucumberContextConfiguration
@ExtendWith(WireMockExtension.class)
@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:Feature")
@ActiveProfiles("test")
public class PaymentATest extends ATest {

    private static final int PORT = 12346;
    private CartDto cartDto;
    private String cartNumber;
    private CreditCardDto creditCardDto;

    private final WireMockConfiguration port = options().port(PORT);
    private final WireMockServer wireMockServer = new WireMockServer(port);

    @MockitoSpyBean
    private EmailCustomerSupport emailCustomerSupport;

    @Before
    public void setUpBefore() {
        setUp();
        wireMockServer.start();
        configureFor("localhost", port.portNumber());
    }

    @After
    public void tearDownAfter() {
        wireMockServer.stop();
    }

    @Override
    protected void initPath() {
        RestAssured.basePath = PaymentController.PATH;
    }

    @Etantdonné("un panier {string} de {float} euros")
    public void unPanierDeEuros(String panierId, float amount) {
        cartDto = new CartDto(panierId, amount);
    }

    @Et("des information de paiement suivant numéro de carte {string}")
    public void desInformationDePaiementSuivantNuméroDeCarte(String cartNumber) {
        this.cartNumber = cartNumber;
    }

    @Et("une date d'expiration {string} et un cryptogramme {string}")
    public void uneDateDExpirationEtUnCryptogramme(String expirationDate, String cypher) {
        this.creditCardDto = new CreditCardDto(cartNumber, expirationDate, cypher);
    }

    @Quand("on valide le paiement")
    public void onValideLePaiement() {
        //@formatter:off
        response = RestAssured.given()
                .log().all()
                .header("Content-Type", ContentType.JSON)
                .body(new PaymentDto(cartDto, creditCardDto))
        .when()
                .post("");
        //@formatter:on
    }

    @Alors("on a une redirection vers la banque avec la transaction bancaire {string}")
    public void onAUneRedirectionVersLaBanque(String transactionId) {
        response.then()
                .log().all()
                .statusCode(200)
                .body("status", is("PENDING"))
                .body("amount", is(cartDto.amount()))
                .body("id", is(nullValue()))
                .body("transactionId", is(transactionId))
                .body("redirectUrl", is("/bank/payments/3ds"));
    }

    @Etque("la banque valide le paiement {string} sans 3DS")
    public void laBanqueValideLePaiementSansDS(String transactionId) {
        wireMockServer.stubFor(post(urlEqualTo("/bank/payments"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                .withRequestBody(equalToJson(String.format("""
                        {
                            "cardNumber": "%s",
                            "expirationDate": "%s",
                            "cypher": "%s",
                            "cartId": "%s",
                            "amount": "%s"
                        }
                        """, creditCardDto.number(), creditCardDto.expirationDate(), creditCardDto.cypher(), cartDto.id(), cartDto.amount())))
                .willReturn(okJson(String.format("""
                        {
                          "id": "%s",
                          "status": "ok",
                          "redirectionUrl": "null"
                        }
                        """, transactionId))));
    }

    @Etque("le panier {string} est transformé en commande {string}")
    public void lePanierEstTransforméEnCommande(String cartId, String orderId) {
        wireMockServer.stubFor(post(urlPathEqualTo("/orders"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                .withRequestBody(equalToJson(String.format("""
                        {
                            "cartId": "%s",
                            "amount": "%s"
                        }
                        """, cartId, cartDto.amount())))
                .willReturn(okJson("""
                        {
                          "status": "ok",
                          "id": "%s",
                          "amount": "%s"
                        }
                        """.formatted(orderId, cartDto.amount()))));
    }

    @Alors("on est bien redirigé vers la page de confirmation de commande {string} d'un montant de {float} euros avec une transaction bancaire {string}")
    public void onEstBienRedirigéVersLaPageDeConfirmation(String orderId, float amount, String transactionId) {
        var paymentResultDto = response
                .then()
                .log().all()
                .statusCode(200)
                .body("status", is("SUCCESS"))
                .body("id", is(orderId))
                .body("amount", is(amount))
                .body("transactionId", is(transactionId))
                .body("redirectUrl", is("/confirmation/" + orderId + "?amount=" + amount))
                .extract().as(PaymentResultDto.class);

        String confirmationUrl = paymentResultDto.redirectUrl();

        RestAssured.basePath = "/";
        //@formatter:off
        response = RestAssured.given()
                .log().all()
                .header("Content-Type", ContentType.JSON)
                .body(new PaymentDto(cartDto, creditCardDto))
        .when()
                .get(confirmationUrl);
        //@formatter:on
    }

    @Alors("on obtient une commande {string} d'un montant de {float} euros")
    public void onObtientUneCommandeDUnMontantDeEuros(String orderId, float amount) {
        String html = response
                .then()
                .log().all()
                .statusCode(200)
                .extract().asString();

        assertThat(html)
                .as("Confirmation HTML")
                .isEqualTo(String.format("""
                        <!DOCTYPE html>
                        <html lang="fr" xmlns:sec="http://www.w3.org/1999/xhtml">
                        
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Confirmation de commande</title>
                        </head>
                        <body>
                            <h1>Confirmation de commande</h1>
                            <p>Votre commande a été confirmée avec succès.</p>
                            <p>Numéro de la commande : <span>%s</span></p>
                            <p>Montant : <span>%s</span> €</p>
                        </body>
                        </html>
                        """, orderId, amount));
    }

    @Etque("la banque ne valide pas le paiement {string} sans 3DS")
    public void laBanqueNeValidePasLePaiementSansDS(String transactionId) {
        wireMockServer.stubFor(post(urlEqualTo("/bank/payments"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                .withRequestBody(equalToJson(String.format("""
                        {
                            "cardNumber": "%s",
                            "expirationDate": "%s",
                            "cypher": "%s",
                            "cartId": "%s",
                            "amount": "%s"
                        }
                        """, creditCardDto.number(), creditCardDto.expirationDate(), creditCardDto.cypher(), cartDto.id(), cartDto.amount())))
                .willReturn(okJson(String.format("""
                        {
                          "id": "%s",
                          "status": "ko",
                          "redirectionUrl": "null"
                        }
                        """, transactionId))));
    }

    @Alors("on reste sur le paiement {string} de {int} euros")
    public void onResteSurLePaiementDeEuros(String cartId, float amount) {
        response
                .then()
                .log().all()
                .statusCode(200)
                .body("status", is("FAILED"))
                .body("id", is(nullValue()))
                .body("transactionId", is(nullValue()))
                .body("redirectUrl", is(nullValue()))
                .body("amount", is(nullValue()));
    }

    @Et("le panier {string} n'a pas été transformé en commande")
    public void lePanierNAPasÉtéTransforméEnCommande(String cartId) {
        wireMockServer.verify(0, postRequestedFor(urlPathEqualTo("/orders"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json")));
    }

    @Etque("le panier {string} n'est pas transformé en commande")
    public void lePanierNEstPasTransforméEnCommande(String cartId) {
        wireMockServer.stubFor(post(urlPathEqualTo("/orders"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                .withRequestBody(equalToJson(String.format("""
                        {
                            "cartId": "%s",
                            "amount": "%s"
                        }
                        """, cartId, cartDto.amount())))
                .willReturn(okJson("""
                        {
                          "status": "ko"
                        }
                        """)));
    }

    @Etque("on la transaction bancaire {string} est annulée")
    public void onLaTransactionBancaireEstAnnulée(String transactionId) {
        wireMockServer.stubFor(delete(urlPathTemplate("/bank/payments/{transactionId}"))
                .withPathParam("transactionId", equalTo(transactionId))
                .withQueryParam("amount", equalTo(String.valueOf(cartDto.amount())))
                .willReturn(okJson("""
                        {
                          "status": "ok"
                        }
                        """)));
    }

    @Et("on a bien annulé la transaction bancaire {string}")
    public void onABienAnnuléLaTransactionBancaire(String transactionId) {
        wireMockServer.verify(1, deleteRequestedFor(urlPathEqualTo("/bank/payments/" + transactionId))
                .withQueryParam("amount", equalTo(String.valueOf(cartDto.amount()))));
    }

    @Etque("la banque fait une redirection 3DS pour un transaction bancaire {string}")
    public void laBanqueValideLePaiementAvecDS(String transactionId) {
        wireMockServer.stubFor(post(urlEqualTo("/bank/payments"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                .withRequestBody(equalToJson(String.format("""
                        {
                            "cardNumber": "%s",
                            "expirationDate": "%s",
                            "cypher": "%s",
                            "cartId": "%s",
                            "amount": "%s"
                        }
                        """, creditCardDto.number(), creditCardDto.expirationDate(), creditCardDto.cypher(), cartDto.id(), cartDto.amount())))
                .willReturn(okJson(String.format("""
                        {
                          "id": "%s",
                          "status": "PENDING",
                          "redirectionUrl": "/bank/payments/3ds"
                        }
                        """, transactionId))));
    }

    @Etque("la validation du paiement 3DS {string} est {string}")
    public void laValidationDuPaiementDSEstKO(String transactionId, String status) {
        wireMockServer.stubFor(post(urlEqualTo("/bank/payments"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                .withRequestBody(equalToJson(String.format("""
                        {
                            "cardNumber": "%s",
                            "expirationDate": "%s",
                            "cypher": "%s",
                            "cartId": "%s",
                            "amount": "%s"
                        }
                        """, creditCardDto.number(), creditCardDto.expirationDate(), creditCardDto.cypher(), cartDto.id(), cartDto.amount())))
                .willReturn(okJson(String.format("""
                        {
                          "id": "%s",
                          "status": "%s",
                          "redirectionUrl": "/api/payment/cart/confirmation?transactionId=%s&status=ko&cartId=%s&amount=%s"
                        }
                        """, transactionId, status, transactionId, cartDto.id(), cartDto.amount()))));
    }

    @Quand("on revient sur la billetterie avec la transaction bancaire {string} avec le status 3DS {string}")
    public void onRevientSurLaBilletterie(String transactionId, String status) {
        //@formatter:off
        response = RestAssured.given()
                .log().all()
                .header("Content-Type", ContentType.JSON)
                .body(new PaymentDto(cartDto, creditCardDto))
        .when()
                .get("/cart/confirmation?transactionId=" + transactionId
                     + "&status=" + status
                     + "&cartId=" + cartDto.id()
                     + "&amount=" + cartDto.amount());
        //@formatter:on
    }

    @Alors("on revient sur le paiement avec le panier {string} de {float} euros")
    public void onRevientSurLePaiementAvecLePanierDeEuros(String cartId, float amount) {
        var model = response
                .then()
                .log().all()
                .statusCode(200)
                .extract().asString();

        assertThat(model)
                .as("Html")
                .isEqualTo(String.format("""
                        <!DOCTYPE html>
                        <html lang="fr" xmlns:sec="http://www.w3.org/1999/xhtml">
                        
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Panier</title>
                        </head>
                        <body>
                            <h1>Panier</h1>
                            <span class="red">Une erreur est survenue lors du paiement</span>
                            <p>Montant :<span>%s</span> €</p>
                        </body>
                        </html>
                        """, amount));
    }

    @Etque("on la transaction bancaire {string} n'est pas annulée")
    public void onLaTransactionBancaireNEstPasAnnulée(String transactionId) {
        wireMockServer.stubFor(delete(urlPathTemplate("/bank/payments/{transactionId}"))
                .withPathParam("transactionId", equalTo(transactionId))
                .withQueryParam("amount", equalTo(String.valueOf(cartDto.amount())))
                .willReturn(okJson("""
                        {
                          "status": "ko"
                        }
                        """)));
    }

    @Et("le support client est notifié qu'il faut annuler la transaction bancaire {string} à la main")
    public void leSupportClientEstNotifiéQuIlFautAnnulerLaTransactionBancaireÀLaMain(String transactionId) {
        Mockito.verify(emailCustomerSupport).alertTransactionFailure(transactionId, cartDto.id(), cartDto.amount());
    }
}
