package com.billetterie.payment.infrastructure.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "email")
public record EmailConfiguration(String host, int port, String username, String password) {
}
