package com.billetterie.payment.domain;

public interface CustomerSupport {
    void alertTransactionFailure(String transactionId, String cartId, Float amount);
}
