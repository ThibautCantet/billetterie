package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.use_case.PayCommand;

public record ValidationRequested(PaymentStatus status, String transactionId, String redirectUrl, Float amount, CartType cartType) implements Event {
    public static ValidationRequested of(Transaction transaction, PayCommand command) {
        return new ValidationRequested(PaymentStatus.PENDING, transaction.id(), transaction.redirectionUrl() + "&cartType=" + command.cartType().name().toLowerCase(), command.amount(), command.cartType());
    }
}
