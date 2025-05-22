package com.cantet.thibaut.payment.domain;

import com.cantet.thibaut.payment.common.cqrs.event.Event;

public record ValidationRequested(PaymentStatus status, String transactionId, String redirectUrl, Float amount) implements Event {
}
