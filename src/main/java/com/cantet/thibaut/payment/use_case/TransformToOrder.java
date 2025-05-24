package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.common.cqrs.command.CommandHandler;
import com.cantet.thibaut.payment.common.cqrs.command.CommandResponse;
import com.cantet.thibaut.payment.common.cqrs.event.Event;
import com.cantet.thibaut.payment.domain.CancelTransactionFailed;
import com.cantet.thibaut.payment.domain.Order;
import com.cantet.thibaut.payment.domain.OrderCreated;
import com.cantet.thibaut.payment.domain.OrderNotCreated;
import com.cantet.thibaut.payment.domain.Orders;
import com.cantet.thibaut.payment.domain.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransformToOrder implements CommandHandler<TransformToOrderCommand, CommandResponse<Event>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformToOrder.class);

    private final Orders orders;

    public TransformToOrder(Orders orders) {
        this.orders = orders;
    }

    public CommandResponse<Event> execute(TransformToOrderCommand command) {
        Order order = orders.transformToOrder(command.cartId(), command.amount());

        LOGGER.info("Cart transformed to order: {}", order.id());
        return new CommandResponse<>(new OrderCreated(
                PaymentStatus.SUCCESS,
                command.transactionId(),
                String.format("/confirmation/%s?amount=%s", order.id(), command.amount()),
                order.id(),
                command.amount()));
    }

    @Override
    public Class listenTo() {
        return TransformToOrderCommand.class;
    }

    public static String getErrorCartUrl(String cartId, float amount) {
        return "/cart?error=true&cartId=" + cartId + "&amount=" + amount;
    }
}
