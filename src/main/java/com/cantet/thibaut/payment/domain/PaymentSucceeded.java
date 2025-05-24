package com.cantet.thibaut.payment.domain;

import com.cantet.thibaut.payment.common.cqrs.event.Event;

public record PaymentSucceeded(PaymentStatus status, String transactionId, String cartId, Float amount) implements Event {
}
