package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.Command;

public record AlertTransactionFailureCommand(String transactionId, String cartId, Float amount) implements Command {
}
