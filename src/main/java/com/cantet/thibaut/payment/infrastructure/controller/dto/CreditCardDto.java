package com.cantet.thibaut.payment.infrastructure.controller.dto;

public record CreditCardDto(String number, String expirationDate, String cypher) {
}
