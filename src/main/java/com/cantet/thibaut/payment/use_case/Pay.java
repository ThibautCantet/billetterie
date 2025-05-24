package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.common.cqrs.command.CommandHandler;
import com.cantet.thibaut.payment.common.cqrs.command.CommandResponse;
import com.cantet.thibaut.payment.common.cqrs.event.Event;
import com.cantet.thibaut.payment.domain.Bank;
import com.cantet.thibaut.payment.domain.Payment;
import com.cantet.thibaut.payment.domain.PaymentSucceeded;
import com.cantet.thibaut.payment.domain.TransactionFailed;
import com.cantet.thibaut.payment.domain.ValidationRequested;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        if (transaction.isPending()) {
            ValidationRequested validationRequested = new ValidationRequested(
                    transaction.status(),
                    transaction.id(),
                    transaction.redirectionUrl(),
                    command.amount());
            LOGGER.info("Transaction is pending: {}", validationRequested);
            return new CommandResponse<>(validationRequested);
        } else if (!transaction.hasSucceeded()) {
            TransactionFailed failedTransaction = new TransactionFailed(
                    transaction.status(),
                    transaction.id());
            LOGGER.info("Transaction failed: {}", failedTransaction);
            return new CommandResponse<>(failedTransaction);
        } else {
            LOGGER.info("Transaction for cart transactionId {} succeeded, with transaction transactionId:{}", command.cartId(), transaction.id());

            return new CommandResponse<>(new PaymentSucceeded(
                    transaction.status(),
                    transaction.id(),
                    command.cartId(),
                    command.amount()));
        }
    }

    @Override
    public Class listenTo() {
        return PayCommand.class;
    }
}
