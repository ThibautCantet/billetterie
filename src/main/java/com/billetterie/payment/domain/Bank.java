package com.billetterie.payment.domain;

public interface Bank {
    Transaction pay(Payment payment);

    boolean cancel(String transactionId, Float amount);
}
