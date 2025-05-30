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

    public CommandResponse<Event> handle(PayCommand command) {
        Payment payment = new Payment(command.cartId(), command.cardNumber(), command.expirationDate(), command.cypher(), command.amount());
        var transaction = bank.pay(payment);
        if (transaction.isPending()) {
            return new CommandResponse<>(new ValidationRequested(PaymentStatus.PENDING,
                    transaction.id(), transaction.redirectionUrl(), command.amount()));
        } else if (!transaction.hasSucceeded()) {
            return new CommandResponse<>(new TransactionFailed(PaymentStatus.FAILED, transaction.id()));
        } else {
            return new CommandResponse<>(new PaymentSucceeded(PaymentStatus.SUCCESS,
                    transaction.id(), command.cartId(), command.amount()));
        }
    }

    @Override
    public Class listenTo() {
        return PayCommand.class;
    }


}
