package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.Command;

public record SendConfirmationEmailCommand(String email, String orderId, Float amount) implements Command {
}
