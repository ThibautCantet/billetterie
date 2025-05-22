package com.cantet.thibaut.payment.domain;

import com.cantet.thibaut.payment.common.cqrs.event.Event;

public record CancelTransactionFailed(String transactionId) implements Event {
}
