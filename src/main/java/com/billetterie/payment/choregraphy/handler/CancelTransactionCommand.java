package com.billetterie.payment.choregraphy.handler;

import com.billetterie.payment.common.cqrs.command.Command;

public record CancelTransactionCommand(String transactionId, String cartId, Float amount) implements Command {
}
