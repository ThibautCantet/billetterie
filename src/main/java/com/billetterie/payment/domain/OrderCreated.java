package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;

public record OrderCreated(PaymentStatus status, String transactionId, String redirectUrl, String orderId, float amount, CartType cartType) implements Event {
    public static OrderCreated of(String transactionId, String orderId, Float amount, CartType cartType, String url) {
        return new OrderCreated(
                PaymentStatus.SUCCESS,
                transactionId,
                url,
                orderId,
                amount,
                cartType);
    }
}
