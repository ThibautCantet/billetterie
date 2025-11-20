package com.billetterie.payment.domain;

import com.billetterie.payment.use_case.TransformToOrderCommand;

public record ClassicOrderCreated(PaymentStatus status, String transactionId, String redirectUrl, String orderId, float amount) implements OrderCreated {
    public static ClassicOrderCreated of(String transactionId, String orderId, Float amount) {
        return new ClassicOrderCreated(
                PaymentStatus.SUCCESS,
                transactionId,
                String.format("/confirmation/%s?amount=%s", orderId, amount),
                orderId,
                amount
        );
    }

    public static ClassicOrderCreated of(TransformToOrderCommand command, String orderId) {
        return new ClassicOrderCreated(
                PaymentStatus.SUCCESS,
                command.transactionId(),
                String.format("/confirmation/%s?amount=%s", orderId, command.amount()),
                orderId,
                command.amount()
        );
    }
}
