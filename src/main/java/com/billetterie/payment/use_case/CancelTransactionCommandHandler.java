package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.CancelTransactionFailed;
import com.billetterie.payment.domain.CancelTransactionSucceeded;
import org.springframework.stereotype.Service;

@Service
public class CancelTransactionCommandHandler implements CommandHandler<CancelTransactionCommand, CommandResponse<Event>> {
    private final Bank bank;

    public CancelTransactionCommandHandler(Bank bank) {
        this.bank = bank;
    }

    @Override
    public CommandResponse<Event> handle(CancelTransactionCommand command) {
        var cancel = bank.cancel(command.transactionId(), command.amount());
        if (cancel) {
            return new CommandResponse<>(new CancelTransactionSucceeded(command.transactionId()));
        }
        return new CommandResponse<>(CancelTransactionFailed.of(command));
    }

    @Override
    public Class<CancelTransactionCommand> listenTo() {
        return CancelTransactionCommand.class;
    }
}
