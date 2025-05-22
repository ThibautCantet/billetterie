package com.cantet.thibaut.payment.domain;

import java.io.Serializable;

import com.cantet.thibaut.payment.common.cqrs.event.Event;

public record CancelTransactionSucceeded(String transactionId) implements Event {
}
