package com.billetterie.payment.use_case;

public record TransformToOrderCommand(String transactionId, String cartId, float amount) {
}
