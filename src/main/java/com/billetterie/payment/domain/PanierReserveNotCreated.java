package com.billetterie.payment.domain;


import com.billetterie.payment.use_case.TransformToOrderCommand;

public record PanierReserveNotCreated(String transactionId, float amount, String redirectUrl, String cartId) implements OrderNotCreated {
    public static PanierReserveNotCreated of(String transactionId, float amount, String cartId) {
        return new PanierReserveNotCreated(transactionId,
                amount,
                String.format("/panier-reserve-error?cartId=%s&amount=%s", cartId, amount),
                cartId
        );
    }

    public static PanierReserveNotCreated of(TransformToOrderCommand command) {
        return new PanierReserveNotCreated(
                command.transactionId(),
                command.amount(),
                String.format("/panier-reserve-error?cartId=%s&amount=%s", command.cartId(), command.amount()),
                command.cartId());
    }
}
