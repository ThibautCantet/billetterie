package com.billetterie.payment.listener;

import com.billetterie.payment.common.cqrs.command.Command;
import com.billetterie.payment.common.cqrs.event.EventHandlerCommand;
import com.billetterie.payment.domain.CancelTransactionFailed;

public class CancelTransactionFailedListener extends EventHandlerCommand<CancelTransactionFailed> {

    @Override
    public Command handle(CancelTransactionFailed event) {
        //TODO: return a AlertTransactionFailureCommand
        return null;
    }

    @Override
    public Class<CancelTransactionFailed> listenTo() {
        return CancelTransactionFailed.class;
    }
}
