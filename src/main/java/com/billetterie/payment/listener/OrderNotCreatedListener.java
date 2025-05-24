package com.billetterie.payment.listener;

import com.billetterie.payment.common.cqrs.command.Command;
import com.billetterie.payment.common.cqrs.event.EventHandlerCommand;
import com.billetterie.payment.domain.OrderNotCreated;

public class OrderNotCreatedListener extends EventHandlerCommand<OrderNotCreated> {

    @Override
    public Command handle(OrderNotCreated event) {
        //TODO: return a CancelTransactionCommand
        return null;
    }

    @Override
    public Class<OrderNotCreated> listenTo() {
        return null;
    }
}
