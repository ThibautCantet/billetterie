package com.billetterie.payment.listener;

import com.billetterie.payment.common.cqrs.command.Command;
import com.billetterie.payment.common.cqrs.event.EventHandlerCommand;
import com.billetterie.payment.domain.OrderNotCreated;
import com.billetterie.payment.use_case.CancelTransactionCommand;

public class OrderNotCreatedListener extends EventHandlerCommand<OrderNotCreated> {

    @Override
    public Command execute(OrderNotCreated event) {
        return new CancelTransactionCommand(event.transactionId(), event.cartId(), event.amount());
    }

    @Override
    public Class<OrderNotCreated> listenTo() {
        return OrderNotCreated.class;
    }
}
