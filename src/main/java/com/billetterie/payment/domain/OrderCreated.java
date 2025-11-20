package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;

public sealed interface OrderCreated extends Event permits ClassicOrderCreated, PanierReserveCreated {
    PaymentStatus status();

    String transactionId();

    String redirectUrl();

    String orderId();

    float amount();
}
