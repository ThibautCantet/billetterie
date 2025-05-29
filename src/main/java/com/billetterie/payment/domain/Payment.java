package com.billetterie.payment.domain;

public record Payment(String cartId, String number, String expirationDate, String cypher, float amount) {
}
