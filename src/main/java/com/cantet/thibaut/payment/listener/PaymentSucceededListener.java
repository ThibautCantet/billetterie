package com.cantet.thibaut.payment.listener;

import com.cantet.thibaut.payment.common.cqrs.command.Command;
import com.cantet.thibaut.payment.common.cqrs.event.EventHandlerCommand;
import com.cantet.thibaut.payment.domain.PaymentSucceeded;
import com.cantet.thibaut.payment.use_case.TransformToOrderCommand;

public class PaymentSucceededListener extends EventHandlerCommand<PaymentSucceeded> {

    public PaymentSucceededListener() {
    }

    @Override
    public Command execute(PaymentSucceeded event) {
        return new TransformToOrderCommand(event.transactionId(), event.cartId(), event.amount());
    }

    @Override
    public Class<PaymentSucceeded> listenTo() {
        return PaymentSucceeded.class;
    }
}
