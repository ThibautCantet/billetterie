package com.billetterie.payment.common.cqrs.middleware.command;


import com.billetterie.payment.common.cqrs.command.Command;

public class UnmatchedCommandHandlerException extends RuntimeException {
    public <C extends Command> UnmatchedCommandHandlerException(C command) {

    }
}
