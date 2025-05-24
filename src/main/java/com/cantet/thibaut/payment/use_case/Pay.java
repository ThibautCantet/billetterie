package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.common.cqrs.command.CommandHandler;
import com.cantet.thibaut.payment.common.cqrs.command.CommandResponse;
import com.cantet.thibaut.payment.common.cqrs.event.Event;
import com.cantet.thibaut.payment.domain.Bank;
import com.cantet.thibaut.payment.domain.Payment;
import com.cantet.thibaut.payment.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class Pay implements CommandHandler<PayCommand, CommandResponse<Event>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);
    private final Bank bank;

    public Pay(Bank bank) {
        this.bank = bank;
    }

    public CommandResponse<Event> execute(PayCommand command) {
        Payment payment = new Payment(command.cardNumber(), command.expirationDate(), command.cypher(), command.cartId(), command.amount());
        var transaction = bank.pay(payment);

        return null;
    }

    @Override
    public Class listenTo() {
        return null;
    }
}
