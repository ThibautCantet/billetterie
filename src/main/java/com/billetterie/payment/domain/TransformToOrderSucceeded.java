package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;

public record TransformToOrderSucceeded(String email, String orderId, Float amount) implements Event {
}
