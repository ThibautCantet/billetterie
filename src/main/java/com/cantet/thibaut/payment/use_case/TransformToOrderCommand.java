package com.cantet.thibaut.payment.use_case;

public record TransformToOrderCommand(String transactionId, String cartId, float amount) {
}
