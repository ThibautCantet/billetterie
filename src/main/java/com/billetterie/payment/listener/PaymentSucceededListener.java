package com.billetterie.payment.listener;

import com.billetterie.payment.common.cqrs.command.Command;
import com.billetterie.payment.common.cqrs.event.EventHandlerCommand;
import com.billetterie.payment.domain.PaymentSucceeded;

public class PaymentSucceededListener extends EventHandlerCommand<PaymentSucceeded> {

    public PaymentSucceededListener() {
    }

    @Override
    public Command handle(PaymentSucceeded event) {
        //TODO: return a TransformToOrderCommand
        //TODO: then implement CommandBusFactory and PaymentController
        return null;
    }

    @Override
    public Class<PaymentSucceeded> listenTo() {
        return null;
    }
}
