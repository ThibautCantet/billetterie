package com.billetterie.payment.infrastructure.controller.dto;

public record PaymentDto(CartDto cartDto, CreditCardDto creditCardDto) {
}
