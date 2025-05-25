package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;

public record TransactionFailed(PaymentStatus status, String id) implements Event {
    public static TransactionFailed of(String transactionId) {
        return new TransactionFailed(PaymentStatus.FAILED, transactionId);
    }
}
