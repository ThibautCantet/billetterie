package com.cantet.thibaut.payment.infrastructure.client;

public record CancelResponse(String status) {
    public boolean isCanceled() {
        return status.equals("ok");
    }
}
