package com.billetterie.payment.infrastructure.controller.dto;

public record PaymentDto(String email, CartDto cartDto, CreditCardDto creditCardDto) {
}
