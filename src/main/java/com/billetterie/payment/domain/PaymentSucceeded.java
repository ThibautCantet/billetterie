package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.use_case.PayCommand;

public record PaymentSucceeded(PaymentStatus status, String transactionId, String cartId, Float amount) implements Event {
    public static PaymentSucceeded of(Transaction transaction, PayCommand command) {
        return new PaymentSucceeded(PaymentStatus.SUCCESS, transaction.id(), command.cartId(), command.amount());
    }
}
