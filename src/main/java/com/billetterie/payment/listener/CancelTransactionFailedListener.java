package com.billetterie.payment.listener;

import com.billetterie.payment.common.cqrs.command.Command;
import com.billetterie.payment.common.cqrs.event.EventHandlerCommand;
import com.billetterie.payment.domain.CancelTransactionFailed;
import com.billetterie.payment.use_case.AlertTransactionFailureCommand;

public class CancelTransactionFailedListener extends EventHandlerCommand<CancelTransactionFailed> {

    @Override
    public Command execute(CancelTransactionFailed event) {
        //TODO: return a CancelTransactionFailedCommand
        return null;
    }

    @Override
    public Class<CancelTransactionFailed> listenTo() {
        return null;
    }
}
