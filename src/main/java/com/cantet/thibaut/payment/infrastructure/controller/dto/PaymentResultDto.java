package com.cantet.thibaut.payment.infrastructure.controller.dto;

import com.cantet.thibaut.payment.domain.PaymentStatus;

public record PaymentResultDto(PaymentStatus status, String id, Float amount, String transactionId, String redirectUrl) {
    public PaymentResultDto(PaymentStatus status) {
        this(status, null, null, null, null);
    }
}
