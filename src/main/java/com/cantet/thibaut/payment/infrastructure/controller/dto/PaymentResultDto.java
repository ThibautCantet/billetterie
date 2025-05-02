package com.cantet.thibaut.payment.infrastructure.controller.dto;

public record PaymentResultDto(com.cantet.thibaut.payment.domain.PaymentStatus status, String id, float amount, String transactionId, String redirectUrl) {
}
