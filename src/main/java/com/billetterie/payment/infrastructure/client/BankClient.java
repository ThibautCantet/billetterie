package com.billetterie.payment.infrastructure.client;

import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.Payment;
import com.billetterie.payment.domain.PaymentStatus;
import com.billetterie.payment.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BankClient implements Bank {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankClient.class);

    @Value(value = "${bank.url}")
    private String bankUrl;

    private final RestTemplate restTemplate;

    public BankClient() {
        this.restTemplate = new RestTemplate();
    }

    public Transaction pay(Payment payment) {
        String url = bankUrl + "/bank/payments";

        var headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Object> request = new HttpEntity<>(new PaymentRequest(payment), headers);

        try {
            LOGGER.debug("Paying with request {}",  request);
            ResponseEntity<TransactionResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    TransactionResponse.class
            );

            return response.getBody().toTransaction();
        } catch (Exception e) {
            LOGGER.error("Error while processing payment with request {}", request, e);
            return new Transaction(null, PaymentStatus.FAILED, null);
        }
    }

    @Override
    public boolean cancel(String transactionId, Float amount) {
        String url = bankUrl + "/bank/payments/" + transactionId + "?amount=" + amount;

        try {
            ResponseEntity<CancelResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    null,
                    CancelResponse.class
            );

            return response.getBody().isCanceled();
        } catch (Exception e) {
            LOGGER.error("Error while canceling transaction {}", transactionId, e);
            return false;
        }
    }

}
