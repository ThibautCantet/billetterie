package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.CartType;

public record PayAndTransformToOrderCommand(String cartId, String cardNumber, String expirationDate, String cypher, float amount, CartType cartType) {
}
