package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.Command;

public record TransformToOrderCommand(String transactionId, String cartId, float amount, String email) implements Command {
}
