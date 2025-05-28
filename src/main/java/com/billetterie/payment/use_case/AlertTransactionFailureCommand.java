package com.billetterie.payment.use_case;

public record AlertTransactionFailureCommand(String transactionId, String cartId, Float amount) {
}
