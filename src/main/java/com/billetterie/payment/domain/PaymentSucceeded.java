package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;

public record PaymentSucceeded(PaymentStatus status, String transactionId, String cartId, Float amount) implements Event {
}
