package com.cantet.thibaut.payment.domain;

import com.cantet.thibaut.payment.common.cqrs.event.Event;

public record TransactionFailed(PaymentStatus status, String id) implements Event {
}
