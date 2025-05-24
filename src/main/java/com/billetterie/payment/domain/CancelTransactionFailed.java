package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.use_case.CancelTransactionCommand;

public record CancelTransactionFailed(String transactionId, String cartId, Float amount) implements Event {
    public static CancelTransactionFailed of(CancelTransactionCommand command) {
        return new CancelTransactionFailed(command.transactionId(), command.cartId(), command.amount());
    }
}
