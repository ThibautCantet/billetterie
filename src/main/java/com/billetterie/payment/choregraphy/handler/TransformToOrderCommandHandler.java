package com.billetterie.payment.choregraphy.handler;

import java.util.List;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.Order;
import com.billetterie.payment.domain.OrderCreated;
import com.billetterie.payment.domain.OrderNotCreated;
import com.billetterie.payment.domain.TransformToOrderSucceeded;
import com.billetterie.payment.domain.Orders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransformToOrderCommandHandler implements CommandHandler<TransformToOrderCommand, CommandResponse<Event>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformToOrderCommandHandler.class);

    private final Orders orders;

    public TransformToOrderCommandHandler(Orders orders) {
        this.orders = orders;
    }

    @Override
    public CommandResponse<Event> handle(TransformToOrderCommand command) {
        Order order = orders.transformToOrder(command.cartId(), command.amount());

        if (order.isCompleted()) {
        LOGGER.info("Cart transformed to order: {}", order.id());
        var orderCreated = OrderCreated.of(command.transactionId(), order.id(), command.amount(), command.email());
        var transformToOrderSucceeded = new TransformToOrderSucceeded(command.email(), order.id(), command.amount());
        return new CommandResponse<>(
                List.of(orderCreated, transformToOrderSucceeded));
        }

        LOGGER.warn("Cart not transformed to order: {}", command.cartId());

        var orderNotCreated = new OrderNotCreated(
                command.transactionId(),
                command.amount(),
                getErrorCartUrl(command.cartId(), command.amount()),
                command.cartId());
        LOGGER.info("Cart not transformed into order and redirect to empty cart: {}", orderNotCreated);

        return new CommandResponse<>(orderNotCreated);
    }

    @Override
    public Class<TransformToOrderCommand> listenTo() {
        return TransformToOrderCommand.class;
    }

    public static String getErrorCartUrl(String cartId, float amount) {
        return "/cart?error=true&cartId=" + cartId + "&amount=" + amount;
    }
}
