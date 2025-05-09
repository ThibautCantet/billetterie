package com.cantet.thibaut.payment.infrastructure.client;

import com.cantet.thibaut.payment.domain.Bank;
import com.cantet.thibaut.payment.domain.Payment;
import com.cantet.thibaut.payment.domain.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BankClient implements Bank {

    @Value(value = "${bank.url}")
    private String bankUrl;

    private final RestTemplate restTemplate;

    public BankClient() {
        this.restTemplate = new RestTemplate();
    }

    public Transaction pay(Payment payment) {
        String url = bankUrl + "/bank/payments/";

        var headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Object> request = new HttpEntity<>(new PaymentRequest(payment), headers);

        try {
            ResponseEntity<TransactionResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    TransactionResponse.class
            );

            return response.getBody().toTransaction();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean cancel(String transactionId) {
        String url = bankUrl + "/bank/payments/" + transactionId;

        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    null,
                    Boolean.class
            );

            return response.getBody();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
