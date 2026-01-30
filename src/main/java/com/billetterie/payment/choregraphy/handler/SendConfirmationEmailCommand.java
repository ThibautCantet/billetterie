package com.billetterie.payment.choregraphy.handler;

import com.billetterie.payment.common.cqrs.command.Command;

public record SendConfirmationEmailCommand(String email, String orderId, Float amount) implements Command {
}
