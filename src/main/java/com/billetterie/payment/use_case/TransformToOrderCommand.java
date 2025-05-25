package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.Command;
import com.billetterie.payment.domain.CartType;

public record TransformToOrderCommand(String transactionId, String cartId, float amount, CartType cartType) implements Command {
}
