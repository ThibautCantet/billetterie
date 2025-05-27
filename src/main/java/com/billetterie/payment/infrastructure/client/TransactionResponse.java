package com.billetterie.payment.infrastructure.client;

import com.billetterie.payment.domain.PaymentStatus;
import com.billetterie.payment.domain.Transaction;

public record TransactionResponse(String id, String status, String redirectionUrl) {
    public Transaction toTransaction() {
        return new Transaction(id, map(), redirectionUrl);
    }

    private PaymentStatus map() {
        switch (status) {
            case "ok" -> {
                return PaymentStatus.SUCCESS;
            }
            case "ko" -> {
                return PaymentStatus.FAILED;
            }
            case "PENDING" -> {
                return PaymentStatus.PENDING;
            }
            default -> throw new IllegalArgumentException("Unknown status: " + status);
        }
    }
}
