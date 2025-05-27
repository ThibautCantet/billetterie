package com.billetterie.payment.domain;

public record Payment(String number, String expirationDate, String cypher, String cartId, float amount) {
}
