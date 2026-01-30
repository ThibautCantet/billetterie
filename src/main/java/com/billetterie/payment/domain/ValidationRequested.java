package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;

public record ValidationRequested(PaymentStatus status, String transactionId, String redirectUrl, Float amount) implements Event {
    public static ValidationRequested of(String transactionId, String redirectUrl, Float amount) {
        return new ValidationRequested(PaymentStatus.PENDING, transactionId, redirectUrl, amount);
    }
}
