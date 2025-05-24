package com.cantet.thibaut.payment.listener;

import com.cantet.thibaut.payment.common.cqrs.command.Command;
import com.cantet.thibaut.payment.common.cqrs.event.EventHandlerCommand;
import com.cantet.thibaut.payment.domain.OrderNotCreated;

public class OrderNotCreatedListener extends EventHandlerCommand<OrderNotCreated> {

    @Override
    public Command execute(OrderNotCreated event) {
        return null;
    }

    @Override
    public Class<OrderNotCreated> listenTo() {
        return null;
    }
}
