package com.billetterie.payment.choregraphy.handler;

import java.util.List;

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

    public CommandResponse<Event> handle(CancelTransactionCommand  command) {
        var cancel = bank.cancel(command.transactionId(), command.amount());
        //TODO: whether the cancel is successful or not, return the appropriate event in a CommandResponse
        // CancelTransactionSucceeded with transactionId ou CancelTransactionFailed with transactionId, cartId and amount from the command
        return new CommandResponse<>(List.of());
    }

    @Override
    public Class listenTo() {
        return CancelTransactionCommand.class;
    }
}
