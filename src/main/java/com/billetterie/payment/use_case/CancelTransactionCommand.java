package com.billetterie.payment.use_case;

public record CancelTransactionCommand(String transactionId, Float amount) {
}
