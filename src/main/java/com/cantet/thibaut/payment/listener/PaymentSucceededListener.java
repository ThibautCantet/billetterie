package com.cantet.thibaut.payment.listener;

import com.cantet.thibaut.payment.common.cqrs.command.Command;
import com.cantet.thibaut.payment.common.cqrs.event.EventHandlerCommand;
import com.cantet.thibaut.payment.domain.PaymentSucceeded;

public class PaymentSucceededListener extends EventHandlerCommand<PaymentSucceeded> {

    public PaymentSucceededListener() {
    }

    @Override
    public Command execute(PaymentSucceeded event) {
        return null;
    }

    @Override
    public Class<PaymentSucceeded> listenTo() {
        return null;
    }
}
