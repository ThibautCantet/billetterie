package com.billetterie.payment.common.cqrs.application;

import com.billetterie.payment.common.cqrs.middleware.command.CommandBus;
import com.billetterie.payment.common.cqrs.middleware.command.CommandBusFactory;

public abstract class CommandController {
    private CommandBus commandBus;
    private final CommandBusFactory commandBusFactory;

    public CommandController(CommandBusFactory commandBusFactory) {
        this.commandBusFactory = commandBusFactory;
    }

    protected CommandBus getCommandBus() {
        if (commandBus == null) {
            this.commandBus = commandBusFactory.build();
        }
        return commandBus;
    }
}
