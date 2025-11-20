package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;

public sealed interface OrderNotCreated extends Event permits ClassicOrderNotCreated, PanierReserveNotCreated {
        String transactionId();
        float amount();
        String redirectUrl();
        String cartId();
}
