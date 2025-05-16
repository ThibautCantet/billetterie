package com.cantet.thibaut.payment.infrastructure.client;

import com.cantet.thibaut.payment.domain.Payment;

public record PaymentRequest(
        String cardNumber,
        String expirationDate,
        String cypher,
        String cartId,
        String amount
) {
    public PaymentRequest(Payment payment) {
        this(
                payment.number(),
                payment.expirationDate(),
                payment.cypher(),
                payment.cartId(),
                String.valueOf(payment.amount())
        );
    }
}
