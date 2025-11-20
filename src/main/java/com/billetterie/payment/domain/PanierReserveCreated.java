package com.billetterie.payment.domain;

import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.use_case.TransformToOrderCommand;

public record PanierReserveCreated(PaymentStatus status, String transactionId, String redirectUrl, String orderId, float amount) implements Event, OrderCreated {
    public static PanierReserveCreated of(String transactionId, String orderId, Float amount) {
        return new PanierReserveCreated(
                PaymentStatus.SUCCESS,
                transactionId,
                String.format("/my-orders?id=%s&amount=%s", orderId, amount),
                orderId,
                amount
        );
    }

    public static PanierReserveCreated of(TransformToOrderCommand command, String orderId) {
        return new PanierReserveCreated(
                PaymentStatus.SUCCESS,
                command.transactionId(),
                String.format("/my-orders?id=%s&amount=%s", orderId, command.amount()),
                orderId,
                command.amount()
        );
    }
}
