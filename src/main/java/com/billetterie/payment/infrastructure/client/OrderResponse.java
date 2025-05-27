package com.billetterie.payment.infrastructure.client;

import com.billetterie.payment.domain.Order;

public record OrderResponse(String id, String amount, String status) {
    public Order toOrder() {
        if ("ko".equals(status)) {
            return new Order(null, 0);
        }
        return new Order(id, Float.parseFloat(amount));
    }
}
