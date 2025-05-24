package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.common.cqrs.command.CommandHandler;
import com.cantet.thibaut.payment.common.cqrs.command.CommandResponse;
import com.cantet.thibaut.payment.common.cqrs.event.Event;
import com.cantet.thibaut.payment.domain.Bank;
import org.springframework.stereotype.Service;

@Service
public class CancelTransaction implements CommandHandler<CancelTransactionCommand, CommandResponse<Event>> {
    private final Bank bank;

    public CancelTransaction(Bank bank) {
        this.bank = bank;
    }

    public CommandResponse<Event> execute(CancelTransactionCommand  command) {
        var cancel = bank.cancel(command.transactionId(), command.amount());
        return null;
    }

    @Override
    public Class listenTo() {
        return null;
    }
}
