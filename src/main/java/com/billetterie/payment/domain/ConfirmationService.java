package com.billetterie.payment.domain;

public interface ConfirmationService {
    void send(String email, String orderId, float amount);
}
