package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;

public record OrderCreated(PaymentStatus status, String transactionId, String redirectUrl, String orderId, float amount) implements Event {
    public static OrderCreated of(String transactionId, String orderId, Float amount) {
        return new OrderCreated(
                PaymentStatus.SUCCESS,
                transactionId,
                String.format("/confirmation/%s?amount=%s", orderId, amount),
                orderId,
                amount);
    }
}
