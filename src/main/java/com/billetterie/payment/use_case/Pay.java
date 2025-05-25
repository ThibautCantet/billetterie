package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.Payment;
import org.springframework.stereotype.Service;

@Service
public class Pay implements CommandHandler<PayCommand, CommandResponse<Event>> {
    private final Bank bank;

    public Pay(Bank bank) {
        this.bank = bank;
    }

    public CommandResponse<Event> execute(PayCommand command) {
        Payment payment = new Payment(command.cartId(), command.cardNumber(), command.expirationDate(), command.cypher(), command.amount());
        var transaction = bank.pay(payment);
        //TODO: switch on transaction status and return
        // ValidationRequested, TransactionFailed or PaymentSucceeded
        return null;
    }

    @Override
    public Class listenTo() {
        return null;
    }


}
