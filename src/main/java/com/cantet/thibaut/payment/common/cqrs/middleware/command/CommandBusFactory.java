package com.cantet.thibaut.payment.common.cqrs.middleware.command;

import java.util.List;

import com.cantet.thibaut.payment.common.cqrs.command.CommandHandler;
import com.cantet.thibaut.payment.common.cqrs.event.Event;
import com.cantet.thibaut.payment.common.cqrs.event.EventHandler;
import com.cantet.thibaut.payment.common.cqrs.middleware.event.EventBus;
import com.cantet.thibaut.payment.common.cqrs.middleware.event.EventBusFactory;
import com.cantet.thibaut.payment.domain.Bank;
import com.cantet.thibaut.payment.domain.Orders;
import com.cantet.thibaut.payment.listener.PaymentSucceededListener;
import com.cantet.thibaut.payment.use_case.CancelTransaction;
import com.cantet.thibaut.payment.use_case.Pay;
import com.cantet.thibaut.payment.use_case.TransformToOrder;
import org.springframework.stereotype.Service;

@Service
public class CommandBusFactory {

    private final Orders orders;
    private final Bank bank;

    public CommandBusFactory(Orders orders, Bank bank) {
        this.orders = orders;
        this.bank = bank;
    }

    protected List<CommandHandler> getCommandHandlers() {
        return List.of(
                new Pay(bank),
                new TransformToOrder(orders),
                new CancelTransaction(bank)
        );
    }

    protected List<EventHandler<? extends Event>> getEventHandlers() {
        return List.of(
                new PaymentSucceededListener()
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
