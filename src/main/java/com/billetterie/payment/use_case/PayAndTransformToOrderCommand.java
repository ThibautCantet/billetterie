package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.Command;

public record PayAndTransformToOrderCommand(String cartId, String cardNumber, String expirationDate, String cypher, float amount) implements Command {
}
