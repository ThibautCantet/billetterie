package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;

public record TransactionFailed(PaymentStatus status, String id) implements Event {
    public static TransactionFailed of(String id) {
        return new TransactionFailed(PaymentStatus.FAILED, id);
    }
}
