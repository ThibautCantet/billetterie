package com.billetterie.payment.choregraphy.handler;

import com.billetterie.payment.common.cqrs.command.Command;

public record PayCommand(String cartId,
                         String cardNumber,
                         String expirationDate,
                         String cypher,
                         Float amount,
                         String email) implements Command {
}
