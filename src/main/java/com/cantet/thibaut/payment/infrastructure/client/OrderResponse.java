package com.cantet.thibaut.payment.infrastructure.client;

import com.cantet.thibaut.payment.domain.Order;

public record OrderResponse(String id, String amount) {
    public Order toOrder() {
        return new Order(id, Float.parseFloat(amount));
    }
}
