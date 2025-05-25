package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;

public record OrderNotCreated(String transactionId, float amount, String redirectUrl, String cartId) implements Event {
}
