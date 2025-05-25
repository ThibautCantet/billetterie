package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.Command;

public record PayCommand(String cartId,
                         String cardNumber,
                         String expirationDate,
                         String cypher,
                         Float amount) implements Command {
}
