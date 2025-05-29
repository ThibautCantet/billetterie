package com.billetterie.payment.common.cqrs.middleware.command;

import java.util.List;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.common.cqrs.event.EventHandler;
import com.billetterie.payment.common.cqrs.middleware.event.EventBus;
import com.billetterie.payment.common.cqrs.middleware.event.EventBusFactory;
import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.CustomerSupport;
import com.billetterie.payment.domain.Orders;
import com.billetterie.payment.listener.PaymentSucceededListener;
import com.billetterie.payment.use_case.AlertTransactionFailureHandler;
import com.billetterie.payment.use_case.CancelTransactionCommandHandler;
import com.billetterie.payment.use_case.PayCommandHandler;
import com.billetterie.payment.use_case.TransformToOrderCommandHandler;
import org.springframework.stereotype.Service;

@Service
public class CommandBusFactory {

    private final Bank bank;
    private final Orders orders;
    private final CustomerSupport customerSupport;

    public CommandBusFactory(Bank bank, Orders orders, CustomerSupport customerSupport) {
        this.bank = bank;
        this.orders = orders;
        this.customerSupport = customerSupport;
    }

    protected List<CommandHandler> getCommandHandlers() {
        return List.of(
                new PayCommandHandler(bank),
                new TransformToOrderCommandHandler(orders, bank, customerSupport,
                        new CancelTransactionCommandHandler(bank),
                        new AlertTransactionFailureHandler(customerSupport))
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
