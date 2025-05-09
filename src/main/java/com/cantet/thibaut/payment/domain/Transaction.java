package com.cantet.thibaut.payment.domain;

public record Transaction(String id, PaymentStatus status, String redirectionUrl) {
}
