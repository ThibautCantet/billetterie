package com.billetterie.payment.domain;

import com.billetterie.payment.use_case.TransformToOrderCommand;

public record ClassicOrderNotCreated(String transactionId, float amount, String redirectUrl,
                                     String cartId) implements OrderNotCreated {
    public static ClassicOrderNotCreated of(String transactionId, Float amount, String cartId) {
        return new ClassicOrderNotCreated (transactionId,
                amount,
                String.format("/cart?error=true&cartId=%s&amount=%s", cartId, amount),
                cartId
        );
    }

    public static ClassicOrderNotCreated of(TransformToOrderCommand command) {
        return new ClassicOrderNotCreated (
                command.transactionId(),
                command.amount(),
                String.format("/cart?error=true&cartId=%s&amount=%s", command.cartId(), command.amount()),
                command.cartId()
        );
    }
}
