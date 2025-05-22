package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.common.cqrs.command.Command;

public record AlertTransactionFailureCommand(String transactionId, String cartId, Float amount) implements Command {
}
