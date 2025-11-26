package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.Payment;
import org.springframework.stereotype.Service;

@Service
public class PayCommandHandler implements CommandHandler<PayCommand, CommandResponse<Event>> {
    private final Bank bank;
    private final TransformToOrderCommandHandler transformToOrderCommandHandler;

    public PayCommandHandler(Bank bank, TransformToOrderCommandHandler transformToOrderCommandHandler) {
        this.bank = bank;
        this.transformToOrderCommandHandler = transformToOrderCommandHandler;
    }

    public CommandResponse<Event> handle(PayCommand command) {
        Payment payment = new Payment(command.cartId(), command.cardNumber(), command.expirationDate(), command.cypher(), command.amount());
        var transaction = bank.pay(payment);
        //TODO: switch on transaction status and return
        // ValidationRequested.of, TransactionFailed.of or PaymentSucceeded.of

        return transformToOrderCommandHandler.handle(new TransformToOrderCommand(transaction.id(), command.cartId(), command.amount()));
    }

    @Override
    public Class listenTo() {
        return PayCommand.class;
    }


}
