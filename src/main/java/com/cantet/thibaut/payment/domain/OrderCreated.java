package com.cantet.thibaut.payment.domain;

import com.cantet.thibaut.payment.common.cqrs.event.Event;

public record OrderCreated(PaymentStatus status, String transactionId, String redirectUrl, String orderId, float amount) implements Event {
}
