package com.billetterie.payment.infrastructure.client;

public record CancelResponse(String status) {
    public boolean isCanceled() {
        return status.equals("ok");
    }
}
