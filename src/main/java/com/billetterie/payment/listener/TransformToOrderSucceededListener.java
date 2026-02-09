package com.billetterie.payment.listener;

import com.billetterie.payment.common.cqrs.command.Command;
import com.billetterie.payment.common.cqrs.event.EventHandlerCommand;
import com.billetterie.payment.domain.TransformToOrderSucceeded;
import com.billetterie.payment.use_case.SendConfirmationEmailCommand;

public class TransformToOrderSucceededListener extends EventHandlerCommand<TransformToOrderSucceeded> {

    @Override
    public Command handle(TransformToOrderSucceeded event) {
        return new SendConfirmationEmailCommand(event.email(), event.orderId(), event.amount());
    }

    @Override
    public Class<TransformToOrderSucceeded> listenTo() {
        return TransformToOrderSucceeded.class;
    }
}
