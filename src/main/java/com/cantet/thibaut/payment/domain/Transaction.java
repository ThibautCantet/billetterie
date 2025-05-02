package com.cantet.thibaut.payment.domain;

public record Transaction(String id, String status, String redirectionUrl) {
}
