package com.billetterie.payment.use_case;

public record PayCommand(String cartId,
                         String cardNumber,
                         String expirationDate,
                         String cypher,
                         Float amount) {
}
