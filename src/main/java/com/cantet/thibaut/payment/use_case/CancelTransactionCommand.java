package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.common.cqrs.command.Command;

public record CancelTransactionCommand(String transactionId, Float amount, String redirectUrl) implements Command {
}
