package com.billetterie.payment.choregraphy.handler;

import java.util.List;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.Payment;
import com.billetterie.payment.domain.PaymentStatus;
import com.billetterie.payment.domain.PaymentSucceeded;
import com.billetterie.payment.domain.TransactionFailed;
import com.billetterie.payment.domain.ValidationRequested;
import org.springframework.stereotype.Service;

@Service
public class PayCommandHandler implements CommandHandler<PayCommand, CommandResponse<Event>> {
    private final Bank bank;

    public PayCommandHandler(Bank bank) {
        this.bank = bank;
    }

    public CommandResponse<Event> handle(PayCommand command) {
        Payment payment = new Payment(command.cartId(), command.cardNumber(), command.expirationDate(), command.cypher(), command.amount(), command.email());
        var transaction = bank.pay(payment);
        //TODO: depending of the transaction status transaction.isPending() or transaction.hasSucceeded()
        // return a event of type :
        // - ValidationRequested.of with transaction.id and transaction.redirectionUrl and command.amount
        // - TransactionFailed.of with transaction.if
        // - PaymentSucceeded.of with transaction and command
        return new CommandResponse<>(List.of());
    }

    @Override
    public Class listenTo() {
        return PayCommand.class;
    }


}
