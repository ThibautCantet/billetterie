package com.billetterie.payment.common.cqrs.middleware.command;

import java.util.List;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.common.cqrs.event.EventHandler;
import com.billetterie.payment.common.cqrs.middleware.event.EventBus;
import com.billetterie.payment.common.cqrs.middleware.event.EventBusFactory;
import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.ConfirmationService;
import com.billetterie.payment.domain.CustomerSupport;
import com.billetterie.payment.domain.Orders;
import com.billetterie.payment.choregraphy.listener.CancelTransactionFailedListener;
import com.billetterie.payment.choregraphy.listener.OrderNotCreatedListener;
import com.billetterie.payment.choregraphy.listener.TransformToOrderSucceededListener;
import com.billetterie.payment.choregraphy.listener.PaymentSucceededListener;
import com.billetterie.payment.choregraphy.handler.AlertTransactionFailureCommandHandler;
import com.billetterie.payment.choregraphy.handler.CancelTransactionCommandHandler;
import com.billetterie.payment.choregraphy.handler.PayCommandHandler;
import com.billetterie.payment.choregraphy.handler.SendConfirmationEmailCommandHandler;
import com.billetterie.payment.choregraphy.handler.TransformToOrderCommandHandler;
import org.springframework.stereotype.Service;

@Service
public class CommandBusFactory {

    private final Bank bank;
    private final Orders orders;
    private final CustomerSupport customerSupport;
    private final ConfirmationService confirmationService;

    public CommandBusFactory(Bank bank, Orders orders, CustomerSupport customerSupport, ConfirmationService confirmationService) {
        this.bank = bank;
        this.orders = orders;
        this.customerSupport = customerSupport;
        this.confirmationService = confirmationService;
    }

    protected List<CommandHandler> getCommandHandlers() {
        return List.of(
                //TODO: add Pay and TransformToOrder, CancelTransaction, AlertTransactionFailure and SendConfirmationEmail handlers
        );
    }

    protected List<EventHandler<? extends Event>> getEventHandlers() {
        return List.of(
                //TODO: register PaymentSucceededListener register OrderNotCreatedListener, CancelTransactionFailedListener
                // and TransformToOrderSucceededListener listeners
        );
    }

    public CommandBus build() {
        CommandBusDispatcher commandBusDispatcher = buildCommandBusDispatcher();

        CommandBusLogger commandBusLogger = new CommandBusLogger(commandBusDispatcher);

        EventBus eventBus = buildEventBus();

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
