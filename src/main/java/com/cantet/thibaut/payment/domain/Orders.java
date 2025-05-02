package com.cantet.thibaut.payment.domain;

public interface Orders {
    Order transformToOrder(String cartId, float amount);
}
