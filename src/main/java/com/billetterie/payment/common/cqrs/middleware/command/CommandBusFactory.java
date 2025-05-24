package com.billetterie.payment.common.cqrs.middleware.command;

import java.util.List;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.common.cqrs.event.EventHandler;
import com.billetterie.payment.common.cqrs.middleware.event.EventBus;
import com.billetterie.payment.common.cqrs.middleware.event.EventBusFactory;
import org.springframework.stereotype.Service;

@Service
public class CommandBusFactory {


    public CommandBusFactory() {
    }

    protected List<CommandHandler> getCommandHandlers() {
        return List.of(
                //TODO: add Pay and TransformToOrder handlers
        );
    }

    protected List<EventHandler<? extends Event>> getEventHandlers() {
        return List.of(
                //TODO: add PaymentSucceededListener listener
        );
    }

    public CommandBus build() {
        CommandBusDispatcher commandBusDispatcher = buildCommandBusDispatcher();

        EventBus eventBus = buildEventBus();

        CommandBusLogger commandBusLogger = new CommandBusLogger(commandBusDispatcher);

        return new EventBusDispatcherCommandBus(commandBusLogger, eventBus);
    }

    private EventBus buildEventBus() {
        EventBusFactory eventBusFactory = new EventBusFactory(getEventHandlers());
        return eventBusFactory.build();
    }

    private CommandBusDispatcher buildCommandBusDispatcher() {
        List<CommandHandler> commandHandlers = getCommandHandlers();
        return new CommandBusDispatcher(commandHandlers);
    }
}
