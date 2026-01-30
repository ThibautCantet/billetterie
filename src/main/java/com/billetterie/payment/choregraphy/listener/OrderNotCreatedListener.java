package com.billetterie.payment.choregraphy.listener;

import com.billetterie.payment.choregraphy.handler.CancelTransactionCommand;
import com.billetterie.payment.choregraphy.handler.CancelTransactionCommandHandler;
import com.billetterie.payment.common.cqrs.command.Command;
import com.billetterie.payment.common.cqrs.event.EventHandlerCommand;
import com.billetterie.payment.domain.OrderNotCreated;

public class OrderNotCreatedListener extends EventHandlerCommand<OrderNotCreated> {

    @Override
    public Command handle(OrderNotCreated event) {
        //TODO: return a CancelTransactionCommand with the transactionId, cartId and amount from the event
        return null;
    }

    @Override
    public Class<OrderNotCreated> listenTo() {
        return OrderNotCreated.class;
    }
}
