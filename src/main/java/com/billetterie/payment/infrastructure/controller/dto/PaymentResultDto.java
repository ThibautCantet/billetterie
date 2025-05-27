package com.billetterie.payment.infrastructure.controller.dto;

import com.billetterie.payment.domain.PaymentStatus;

public record PaymentResultDto(PaymentStatus status, String id, Float amount, String transactionId, String redirectUrl) {
    public PaymentResultDto(PaymentStatus status) {
        this(status, null, null, null, null);
    }
}
