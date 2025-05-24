package com.cantet.thibaut.payment.domain;

import com.cantet.thibaut.payment.common.cqrs.event.Event;

public record OrderNotCreated(String transactionId, float amount, String redirectUrl, String cartId) implements Event {
}
