package com.cantet.thibaut.payment.common.cqrs.middleware.command;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cantet.thibaut.payment.common.cqrs.command.Command;
import com.cantet.thibaut.payment.common.cqrs.command.CommandHandler;
import com.cantet.thibaut.payment.common.cqrs.command.CommandResponse;
import com.cantet.thibaut.payment.infrastructure.client.BankClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Optional.*;

public class CommandBusDispatcher implements CommandBus {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandBusDispatcher.class);
    private final Map<Class, CommandHandler> commandHandlers;

    public CommandBusDispatcher(List<? extends CommandHandler> commandHandlers) {
        this.commandHandlers = commandHandlers.stream().collect(Collectors
                .toMap(CommandHandler::listenTo, commandHandler -> commandHandler));
    }

    @Override
    public <R extends CommandResponse, C extends Command> R dispatch(C command) {
        CommandHandler<C, R> commandHandler = this.commandHandlers.get(command.getClass());
        return ofNullable(commandHandler)
                .map(handler -> handler.execute(command))
                .orElseThrow(() -> {
                    LOGGER.error("No command handler found for command: {}", command);
                    return new UnmatchedCommandHandlerException(command);
                });
    }
}
