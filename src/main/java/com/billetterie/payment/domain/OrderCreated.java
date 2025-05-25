package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;

public record OrderCreated(PaymentStatus status, String transactionId, String redirectUrl, String orderId, float amount) implements Event {
}
