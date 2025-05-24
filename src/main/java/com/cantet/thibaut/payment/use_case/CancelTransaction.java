package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.common.cqrs.command.CommandHandler;
import com.cantet.thibaut.payment.common.cqrs.command.CommandResponse;
import com.cantet.thibaut.payment.common.cqrs.event.Event;
import com.cantet.thibaut.payment.domain.Bank;
import com.cantet.thibaut.payment.domain.CancelTransactionFailed;
import com.cantet.thibaut.payment.domain.CancelTransactionSucceeded;
import org.springframework.stereotype.Service;

@Service
public class CancelTransaction implements CommandHandler<CancelTransactionCommand, CommandResponse<Event>> {
    private final Bank bank;

    public CancelTransaction(Bank bank) {
        this.bank = bank;
    }

    public CommandResponse<Event> execute(CancelTransactionCommand  command) {
        boolean cancel = bank.cancel(command.transactionId(), command.amount());
        if (!cancel) {
            return new CommandResponse<>(new CancelTransactionFailed(command.transactionId(), command.cartId(), command.amount()));
        }
        return new CommandResponse<>(new CancelTransactionSucceeded(command.transactionId()));
    }

    @Override
    public Class listenTo() {
        return CancelTransactionCommand.class;
    }
}
