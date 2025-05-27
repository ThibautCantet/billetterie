package com.billetterie.payment.domain;

public interface Orders {
    Order transformToOrder(String cartId, float amount);
}
