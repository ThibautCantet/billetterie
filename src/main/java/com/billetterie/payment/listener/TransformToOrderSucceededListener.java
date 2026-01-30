package com.billetterie.payment.listener;

import com.billetterie.payment.common.cqrs.command.Command;
import com.billetterie.payment.common.cqrs.event.EventHandlerCommand;
import com.billetterie.payment.domain.TransformToOrderSucceeded;

public class TransformToOrderSucceededListener extends EventHandlerCommand<TransformToOrderSucceeded> {

    @Override
    public Command handle(TransformToOrderSucceeded event) {
        //TODO: return a SendConfirmationEmailCommand
        return null;
    }

    @Override
    public Class<TransformToOrderSucceeded> listenTo() {
        return TransformToOrderSucceeded.class;
    }
}

