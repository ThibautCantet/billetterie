package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.Bank;
import org.springframework.stereotype.Service;

@Service
public class CancelTransactionCommandHandler implements CommandHandler<CancelTransactionCommand, CommandResponse<Event>> {
    private final Bank bank;

    public CancelTransactionCommandHandler(Bank bank) {
        this.bank = bank;
    }

    public CommandResponse<Event> handle(CancelTransactionCommand  command) {
        var cancel = bank.cancel(command.transactionId(), command.amount());
        //TODO: depending on the cancel result, return a CommandResponse
        // with CancelTransactionSucceeded or CancelTransactionFailed event
        return null;
    }

    @Override
    public Class listenTo() {
        return null;
    }
}
