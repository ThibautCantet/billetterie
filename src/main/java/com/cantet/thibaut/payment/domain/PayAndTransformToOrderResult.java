package com.cantet.thibaut.payment.domain;

public record PayAndTransformToOrderResult(PaymentStatus status, String transactionId, String redirectUrl, String orderId, float amount) {
}
