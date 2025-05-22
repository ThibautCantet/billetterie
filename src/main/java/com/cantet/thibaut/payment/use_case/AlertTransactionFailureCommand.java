package com.cantet.thibaut.payment.use_case;

public record AlertTransactionFailureCommand(String transactionId, String cartId, Float amount) {
}
