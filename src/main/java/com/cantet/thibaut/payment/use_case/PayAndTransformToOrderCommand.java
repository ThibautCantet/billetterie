package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.common.cqrs.command.Command;

public record PayAndTransformToOrderCommand(String cartId, String cardNumber, String expirationDate, String cypher, float amount) implements Command {
}
