package com.billetterie.payment.choregraphy.listener;

import com.billetterie.payment.common.cqrs.command.Command;
import com.billetterie.payment.common.cqrs.event.EventHandlerCommand;
import com.billetterie.payment.domain.PaymentSucceeded;
import com.billetterie.payment.choregraphy.handler.TransformToOrderCommand;

public class PaymentSucceededListener extends EventHandlerCommand<PaymentSucceeded> {

    @Override
    public Command handle(PaymentSucceeded event) {
        return new TransformToOrderCommand(event.transactionId(), event.cartId(), event.amount(), event.email());
    }

    @Override
    public Class<PaymentSucceeded> listenTo() {
        return PaymentSucceeded.class;
    }
}

