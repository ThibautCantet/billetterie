package com.billetterie.payment.listener;

import com.billetterie.payment.common.cqrs.command.Command;
import com.billetterie.payment.common.cqrs.event.EventHandlerCommand;
import com.billetterie.payment.domain.PaymentSucceeded;

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
