package com.cantet.thibaut.payment.infrastructure.client;

import com.cantet.thibaut.payment.domain.Payment;

public record PaymentRequest(
        String cardNumber,
        String expirationDate,
        String cypher,
        String amount
) {
    public PaymentRequest(Payment payment) {
        this(
                payment.number(),
                payment.expirationDate(),
                payment.cypher(),
                String.valueOf(payment.amount())
        );
    }
}
