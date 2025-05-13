package com.bank.controller;

public record PaymentRequest(
        String cardNumber,
        String expirationDate,
        String cypher,
        String amount
) {

}
