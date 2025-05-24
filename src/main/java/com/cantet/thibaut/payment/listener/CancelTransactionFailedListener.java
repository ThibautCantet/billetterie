package com.cantet.thibaut.payment.listener;

import com.cantet.thibaut.payment.common.cqrs.command.Command;
import com.cantet.thibaut.payment.common.cqrs.event.EventHandlerCommand;
import com.cantet.thibaut.payment.domain.CancelTransactionFailed;
import com.cantet.thibaut.payment.use_case.AlertTransactionFailureCommand;

public class CancelTransactionFailedListener extends EventHandlerCommand<CancelTransactionFailed> {

    @Override
    public Command execute(CancelTransactionFailed event) {
        return null;
    }

    @Override
    public Class<CancelTransactionFailed> listenTo() {
        return null;
    }
}
