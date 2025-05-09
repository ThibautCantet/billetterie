package com.cantet.thibaut.payment.domain;

public interface Bank {
    Transaction pay(Payment payment);

    boolean cancel(String transactionId);
}
