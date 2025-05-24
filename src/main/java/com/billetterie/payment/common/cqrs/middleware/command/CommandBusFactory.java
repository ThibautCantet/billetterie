package com.billetterie.payment.common.cqrs.middleware.command;

import java.util.List;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.common.cqrs.event.EventHandler;
import com.billetterie.payment.common.cqrs.middleware.event.EventBus;
import com.billetterie.payment.common.cqrs.middleware.event.EventBusFactory;
import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.Orders;
import com.billetterie.payment.listener.PaymentSucceededListener;
import com.billetterie.payment.use_case.Pay;
import com.billetterie.payment.use_case.TransformToOrder;
import org.springframework.stereotype.Service;

@Service
public class CommandBusFactory {


    private final Bank bank;
    private final Orders orders;

    public CommandBusFactory(Bank bank, Orders orders) {
        this.bank = bank;
        this.orders = orders;
    }

    protected List<CommandHandler> getCommandHandlers() {
        return List.of(
                new Pay(bank),
                new TransformToOrder(orders)
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
