package com.cantet.thibaut.payment;

import com.cantet.thibaut.ATest;
import com.cantet.thibaut.payment.infrastructure.controller.PaymentController;
import com.cantet.thibaut.payment.infrastructure.controller.dto.CartDto;
import com.cantet.thibaut.payment.infrastructure.controller.dto.CreditCardDto;
import com.cantet.thibaut.payment.infrastructure.controller.dto.PaymentDto;
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
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
@Transactional
@AutoConfigureCache
@AutoConfigureDataJpa
@EnableJpaRepositories
@AutoConfigureTestEntityManager
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

    @Etque("la banque valide le paiement {string} sans 3DS")
    public void laBanqueValideLePaiementSansDS(String transactionId) {
        wireMockServer.stubFor(post(urlEqualTo("/bank/payments/"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                .withRequestBody(equalToJson(String.format("""
                        {
                            "cardNumber": "%s",
                            "expirationDate": "%s",
                            "cypher": "%s",
                            "amount": "%s"
                        }
                        """, creditCardDto.number(), creditCardDto.expirationDate(), creditCardDto.cypher(), cartDto.amount())))
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

    @Alors("on obtient une commande {string} d'un montant de {float} euros avec la transaction bancaire {string}")
    public void onObtientUneCommandeDUnMontantDeEuros(String orderId, float amount, String transactionId) {
        response
                .then()
                .log().all()
                .statusCode(200)
                .body("status", is("SUCCESS"))
                .body("id", is(orderId))
                .body("transactionId", is(transactionId))
                .body("redirectUrl", is("confirmation"))
                .body("amount", is(amount))
        ;
    }

    @Etque("la banque ne valide pas le paiement {string} sans 3DS")
    public void laBanqueNeValidePasLePaiementSansDS(String transactionId) {
        wireMockServer.stubFor(post(urlEqualTo("/bank/payments/"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                .withRequestBody(equalToJson(String.format("""
                        {
                            "cardNumber": "%s",
                            "expirationDate": "%s",
                            "cypher": "%s",
                            "amount": "%s"
                        }
                        """, creditCardDto.number(), creditCardDto.expirationDate(), creditCardDto.cypher(), cartDto.amount())))
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
        wireMockServer.stubFor(delete(urlEqualTo("/bank/payments/" + transactionId))
                .willReturn(okJson("""
                        {
                          "status": "ok"
                        }
                        """)));
    }

    @Et("on a bien annulé la transaction bancaire {string}")
    public void onABienAnnuléLaTransactionBancaire(String transactionId) {
        wireMockServer.verify(1, deleteRequestedFor(urlEqualTo("/bank/payments/" + transactionId)));
    }
}
