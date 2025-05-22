package com.cantet.thibaut.payment.common.cqrs.middleware.command;

import com.cantet.thibaut.payment.common.cqrs.command.Command;
import com.cantet.thibaut.payment.common.cqrs.command.CommandResponse;

public interface CommandBus {
    <R extends CommandResponse, C extends Command> R dispatch(C command);
}
