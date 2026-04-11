package com.billetterie.payment.choregraphy.listener;

import com.billetterie.payment.common.cqrs.command.Command;
import com.billetterie.payment.common.cqrs.event.EventHandlerCommand;
import com.billetterie.payment.domain.OrderNotCreated;
import com.billetterie.payment.choregraphy.handler.CancelTransactionCommand;

public class OrderNotCreatedListener extends EventHandlerCommand<OrderNotCreated> {

    @Override
    public Command handle(OrderNotCreated event) {
        return new CancelTransactionCommand(event.transactionId(), event.cartId(), event.amount());
    }

    @Override
    public Class<OrderNotCreated> listenTo() {
        return OrderNotCreated.class;
    }
}
