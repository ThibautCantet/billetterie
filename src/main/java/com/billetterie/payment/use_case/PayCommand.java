package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.Command;
import com.billetterie.payment.domain.CartType;

public record PayCommand(String cartId,
                         String cardNumber,
                         String expirationDate,
                         String cypher,
                         Float amount,
                         CartType cartType) implements Command {
}
