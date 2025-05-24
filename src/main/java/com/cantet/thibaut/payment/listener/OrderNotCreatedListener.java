package com.cantet.thibaut.payment.listener;

import com.cantet.thibaut.payment.common.cqrs.command.Command;
import com.cantet.thibaut.payment.common.cqrs.event.EventHandlerCommand;
import com.cantet.thibaut.payment.domain.OrderNotCreated;
import com.cantet.thibaut.payment.use_case.CancelTransactionCommand;

public class OrderNotCreatedListener extends EventHandlerCommand<OrderNotCreated> {

    @Override
    public Command execute(OrderNotCreated event) {
        return new CancelTransactionCommand(event.transactionId(), event.amount(), event.redirectUrl(), event.cartId());
    }

    @Override
    public Class<OrderNotCreated> listenTo() {
        return OrderNotCreated.class;
    }
}
