package com.billetterie.payment.listener;

import com.billetterie.payment.common.cqrs.command.Command;
import com.billetterie.payment.common.cqrs.event.EventHandlerCommand;
import com.billetterie.payment.domain.PaymentSucceeded;
import com.billetterie.payment.use_case.TransformToOrderCommand;

public class PaymentSucceededListener extends EventHandlerCommand<PaymentSucceeded> {

    public PaymentSucceededListener() {
    }

    @Override
    public Command execute(PaymentSucceeded event) {
        //TODO: return a TransformToOrderCommand
        //TODO: then implement CommandBusFactory and
        return null;
    }

    @Override
    public Class<PaymentSucceeded> listenTo() {
        return PaymentSucceeded.class;
    }
}
