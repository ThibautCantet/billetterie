package com.billetterie.payment.use_case;

import java.util.List;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
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

    public TransformToOrderCommandHandler(Orders orders) {
        this.orders = orders;
    }

    public CommandResponse<Event> handle(TransformToOrderCommand command) {
        Order order = orders.transformToOrder(command.cartId(), command.amount());

        if (order.isNotCompleted()) {
            LOGGER.warn("Cart not transformed to order: {}", command.cartId());

            var failed = new OrderNotCreated(
                    command.transactionId(),
                    command.amount(),
                    getErrorCartUrl(command.cartId(), command.amount()),
                    command.cartId());
            LOGGER.info("Cart not transformed into order and redirect to empty cart: {}", failed);

            return new CommandResponse<>(failed);
        }

        LOGGER.info("Cart transformed to order: {}", order.id());
        var created = OrderCreated.of(
                command.transactionId(),
                order.id(),
                command.amount());

        return new CommandResponse<>(created);
    }

    @Override
    public Class listenTo() {
        return TransformToOrderCommand.class;
    }

    public static String getErrorCartUrl(String cartId, float amount) {
        return "/cart?error=true&cartId=" + cartId + "&amount=" + amount;
    }
}
