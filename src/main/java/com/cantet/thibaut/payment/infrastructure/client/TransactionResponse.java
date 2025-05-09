package com.cantet.thibaut.payment.infrastructure.client;

import com.cantet.thibaut.payment.domain.PaymentStatus;
import com.cantet.thibaut.payment.domain.Transaction;

public record TransactionResponse(String id, String status, String redirectionUrl) {
    public Transaction toTransaction() {
        return new Transaction(id, status.equals("ko") ? PaymentStatus.FAILED : PaymentStatus.SUCCESS, redirectionUrl);
    }
}
