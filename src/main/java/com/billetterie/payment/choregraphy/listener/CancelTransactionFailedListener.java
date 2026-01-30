package com.billetterie.payment.choregraphy.listener;

import com.billetterie.payment.choregraphy.handler.AlertTransactionFailureCommand;
import com.billetterie.payment.common.cqrs.command.Command;
import com.billetterie.payment.common.cqrs.event.EventHandlerCommand;
import com.billetterie.payment.domain.CancelTransactionFailed;

public class CancelTransactionFailedListener extends EventHandlerCommand<CancelTransactionFailed> {

    @Override
    public Command handle(CancelTransactionFailed event) {
        //TODO: return a AlertTransactionFailureCommand with transactionId, cartId and amount from the event
        return null;
    }

    @Override
    public Class<CancelTransactionFailed> listenTo() {
        return CancelTransactionFailed.class;
    }
}
