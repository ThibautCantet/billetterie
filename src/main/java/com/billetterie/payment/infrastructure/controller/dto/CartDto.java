package com.billetterie.payment.infrastructure.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public record CartDto(String id, float amount, Boolean error) {
    public CartDto(String id, float amount) {
        this(id, amount, null);
    }
}
