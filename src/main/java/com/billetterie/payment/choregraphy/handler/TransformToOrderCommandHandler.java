package com.billetterie.payment.choregraphy.handler;

import java.util.List;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
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
        //TODO: call order.transformToOrder with cartId and amount

        //TODO: check if order.isCompleted() then
        // return 2 events
        // - OrderCreated.of(command.transactionId(), order.id(), command.amount(), command.email());
        // - and new TransformToOrderSucceeded(command.email(), order.id(), command.amount());

        //TODO the order is not completed then return
        // new OrderNotCreated(
        //           command.transactionId(),
        //           command.amount(),
        //           getErrorCartUrl(command.cartId(), command.amount()),
        //           command.cartId());

        return new CommandResponse<>(List.of());
    }

    @Override
    public Class listenTo() {
        return TransformToOrderCommand.class;
    }

    public static String getErrorCartUrl(String cartId, float amount) {
        return "/cart?error=true&cartId=" + cartId + "&amount=" + amount;
    }
}
