package com.cantet.thibaut.payment.infrastructure.client;

public record OrderRequest(String cartId, String amount) {
    public OrderRequest(String cartId, float amount) {
        this(cartId, String.valueOf(amount));
    }
}
