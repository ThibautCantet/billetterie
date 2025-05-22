package com.cantet.thibaut.payment.use_case;

public record CancelTransactionCommand(String transactionId, Float amount) {
}
