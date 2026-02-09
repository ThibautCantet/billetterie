package com.billetterie.payment.use_case;

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

    @Override
    public CommandResponse<Event> handle(PayCommand command) {
        Payment payment = new Payment(command.cartId(), command.cardNumber(), command.expirationDate(), command.cypher(), command.amount(), command.email());
        var transaction = bank.pay(payment);

        return switch (transaction.status()) {
            case PENDING -> new CommandResponse<>(new ValidationRequested(
                    PaymentStatus.PENDING,
                    transaction.id(),
                    transaction.redirectionUrl(),
                    command.amount()));
            case FAILED -> new CommandResponse<>(new TransactionFailed(PaymentStatus.FAILED, transaction.id()));
            case SUCCESS -> new CommandResponse<>(PaymentSucceeded.of(transaction, command));
        };
    }

    @Override
    public Class<PayCommand> listenTo() {
        return PayCommand.class;
    }
}
