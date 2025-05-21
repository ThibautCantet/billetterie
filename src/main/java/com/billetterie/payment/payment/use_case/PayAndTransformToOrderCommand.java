package com.billetterie.payment.payment.use_case;

public record PayAndTransformToOrderCommand(String cartId, String cardNumber, String expirationDate, String cypher, float amount) {
}
