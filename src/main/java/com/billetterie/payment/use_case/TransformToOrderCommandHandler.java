package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.CancelTransactionFailed;
import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.CustomerSupport;
import com.billetterie.payment.domain.Order;
import com.billetterie.payment.domain.OrderCreated;
import com.billetterie.payment.domain.OrderNotCreated;
import com.billetterie.payment.domain.Orders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransformToOrderCommandHandler implements CommandHandler<TransformToOrderCommand, CommandResponse<Event>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformToOrderCommandHandler.class);

    private final Orders orders;
    private final Bank bank;
    private final CustomerSupport customerSupport;
    private final CancelTransactionCommandHandler cancelTransactionCommandHandler;
    private final AlertTransactionFailureCommandHandler alertTransactionFailureCommandHandler;

    public TransformToOrderCommandHandler(Orders orders, Bank bank, CustomerSupport customerSupport, CancelTransactionCommandHandler cancelTransactionCommandHandler, AlertTransactionFailureCommandHandler alertTransactionFailureCommandHandler) {
        this.orders = orders;
        this.bank = bank;
        this.customerSupport = customerSupport;
        this.cancelTransactionCommandHandler = cancelTransactionCommandHandler;
        this.alertTransactionFailureCommandHandler = alertTransactionFailureCommandHandler;
    }

    public CommandResponse<Event> handle(TransformToOrderCommand command) {
        Order order = orders.transformToOrder(command.cartId(), command.amount());

        if (order.isNotCompleted()) {
            //TODO: remove cancelTransaction and alertTransactionFailure use cases
            //TODO: register CancelTransaction, AlertTransactionFailure handlers
            //TODO: register OrderNotCreatedListener and CancelTransactionFailedListener listeners
            //TODO: dispatch TransformToOrderCommand in controller
            LOGGER.warn("Cart not transformed to order: {}", command.cartId());
            var cancel = cancelTransactionCommandHandler.handle(new CancelTransactionCommand(command.transactionId(), command.cartId(), command.amount()));
            if (cancel.first() instanceof CancelTransactionFailed) {
                LOGGER.error("Transaction cancellation failed: {}", command.transactionId());
                alertTransactionFailureCommandHandler.handle(new AlertTransactionFailureCommand(command.transactionId(), command.cartId(), command.amount()));
            } else {
                LOGGER.info("Transaction cancelled: {}", command.transactionId());
            }

            var orderNotCreated = new OrderNotCreated(
                    command.transactionId(),
                    command.amount(),
                    getErrorCartUrl(command.cartId(), command.amount()),
                    command.cartId());
            LOGGER.info("Cart not transformed into order and redirect to empty cart: {}", orderNotCreated);

            return new CommandResponse<>(orderNotCreated);
        }

        LOGGER.info("Cart transformed to order: {}", order.id());
        var orderCreated = OrderCreated.of(
                command.transactionId(),
                order.id(),
                command.amount());

        return new CommandResponse<>(orderCreated);
    }

    @Override
    public Class listenTo() {
        return TransformToOrderCommand.class;
    }

    public static String getErrorCartUrl(String cartId, float amount) {
        return "/cart?error=true&cartId=" + cartId + "&amount=" + amount;
    }
}
