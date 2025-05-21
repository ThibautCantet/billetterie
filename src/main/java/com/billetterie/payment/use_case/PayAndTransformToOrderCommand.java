package com.billetterie.payment.use_case;

public record PayAndTransformToOrderCommand(String cartId, String cardNumber, String expirationDate, String cypher, float amount) {
}
