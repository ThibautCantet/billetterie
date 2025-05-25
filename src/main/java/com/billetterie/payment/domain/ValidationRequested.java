package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;

public record ValidationRequested(PaymentStatus status, String transactionId, String redirectUrl, Float amount) implements Event {
}
