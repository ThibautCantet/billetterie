package com.cantet.thibaut.payment.infrastructure.controller.dto;

public record PaymentDto(CartDto cartDto, CreditCardDto creditCardDto) {
}
