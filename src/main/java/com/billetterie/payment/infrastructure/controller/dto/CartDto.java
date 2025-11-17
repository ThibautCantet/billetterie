package com.billetterie.payment.infrastructure.controller.dto;

import com.billetterie.payment.domain.CartType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public record CartDto(String id, float amount, CartType type) {
    public CartDto(String id, float amount) {
        this(id, amount, CartType.CLASSIC);
    }
}
