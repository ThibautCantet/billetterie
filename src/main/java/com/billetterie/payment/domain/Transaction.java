package com.billetterie.payment.domain;

public record Transaction(String id, PaymentStatus status, String redirectionUrl) {
    public boolean hasSucceeded() {
        return status == PaymentStatus.SUCCESS;
    }

    public boolean isPending() {
        return status == PaymentStatus.PENDING;
    }
}
