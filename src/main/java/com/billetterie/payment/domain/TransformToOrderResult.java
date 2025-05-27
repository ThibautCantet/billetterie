package com.billetterie.payment.domain;

public record TransformToOrderResult(TransformToOrderStatus status, String transactionId, String redirectUrl, String orderId, Float amount) {
}
