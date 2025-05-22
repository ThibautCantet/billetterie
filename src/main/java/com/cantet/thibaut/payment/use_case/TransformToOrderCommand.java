package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.common.cqrs.command.Command;

public record TransformToOrderCommand(String transactionId, String cartId, float amount) implements Command {
}
