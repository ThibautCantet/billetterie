package com.billetterie.payment.choregraphy.handler;

import com.billetterie.payment.common.cqrs.command.Command;

public record TransformToOrderCommand(String transactionId, String cartId, float amount, String email) implements Command {
}
