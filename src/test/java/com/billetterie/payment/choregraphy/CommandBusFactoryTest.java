package com.billetterie.payment.choregraphy;

import java.util.List;

import com.billetterie.payment.choregraphy.handler.AlertTransactionFailureCommandHandler;
import com.billetterie.payment.choregraphy.handler.CancelTransactionCommandHandler;
import com.billetterie.payment.choregraphy.handler.PayCommandHandler;
import com.billetterie.payment.choregraphy.handler.SendConfirmationEmailCommandHandler;
import com.billetterie.payment.choregraphy.handler.TransformToOrderCommandHandler;
import com.billetterie.payment.choregraphy.listener.CancelTransactionFailedListener;
import com.billetterie.payment.choregraphy.listener.OrderNotCreatedListener;
import com.billetterie.payment.choregraphy.listener.PaymentSucceededListener;
import com.billetterie.payment.choregraphy.listener.TransformToOrderSucceededListener;
import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.common.cqrs.event.EventHandler;
import com.billetterie.payment.common.cqrs.middleware.command.CommandBusFactory;
import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.ConfirmationService;
import com.billetterie.payment.domain.CustomerSupport;
import com.billetterie.payment.domain.Orders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommandBusFactoryTest {

    private SutCommandBusFactory commandBusFactory;
    @Mock
    private Bank bank;
    @Mock
    private Orders orders;
    @Mock
    private CustomerSupport customerSupport;
    @Mock
    private ConfirmationService confirmationService;

    @BeforeEach
    void setUp() {
        commandBusFactory = new SutCommandBusFactory(bank, orders, customerSupport, confirmationService);
    }

    @Test
    void should_return_all_registered_command_handlers() {
        var commandHandlers = commandBusFactory.getCommandHandlers();

        assertThat(commandHandlers)
                .hasSize(5)
                .extracting(CommandHandler::getClass)
                .containsExactly(
                        PayCommandHandler.class,
                        TransformToOrderCommandHandler.class,
                        CancelTransactionCommandHandler.class,
                        AlertTransactionFailureCommandHandler.class,
                        SendConfirmationEmailCommandHandler.class
                );
    }

    @Test
    void should_return_all_registered_listeners() {
        var eventHandlers = commandBusFactory.getEventHandlers();

        assertThat(eventHandlers)
                .hasSize(4)
                .extracting(EventHandler::getClass)
                .containsExactly(
                        PaymentSucceededListener.class,
                        OrderNotCreatedListener.class,
                        CancelTransactionFailedListener.class,
                        TransformToOrderSucceededListener.class
                );
    }

    static class SutCommandBusFactory extends CommandBusFactory {
        public SutCommandBusFactory(Bank bank, Orders orders, CustomerSupport customerSupport, ConfirmationService confirmationService) {
            super(bank, orders, customerSupport, confirmationService);
        }

        @Override
        @SuppressWarnings("rawtypes")
        protected List<CommandHandler> getCommandHandlers() {
            return super.getCommandHandlers();
        }

        @Override
        protected List<EventHandler<? extends Event>> getEventHandlers() {
            return super.getEventHandlers();
        }
    }
}
