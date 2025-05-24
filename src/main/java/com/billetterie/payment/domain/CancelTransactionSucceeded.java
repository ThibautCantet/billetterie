package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;

public record CancelTransactionSucceeded(String transactionId) implements Event {
}
