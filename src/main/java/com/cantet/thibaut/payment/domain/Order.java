package com.cantet.thibaut.payment.domain;

public record Order(String id, float amount) {
    public boolean isNotCompleted() {
        return id == null;
    }
}
