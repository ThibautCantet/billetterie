package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.PaymentSucceeded;
import com.billetterie.payment.domain.TransactionFailed;
import com.billetterie.payment.domain.ValidationRequested;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PayAndTransformToOrderCommandHandler implements CommandHandler<PayAndTransformToOrderCommand, CommandResponse<Event>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayAndTransformToOrderCommandHandler.class);

    private final Bank bank;
    private final TransformToOrderCommandHandler transformToOrderCommandHandler;
    private final PayCommandHandler pay;

    public PayAndTransformToOrderCommandHandler(Bank bank, TransformToOrderCommandHandler transformToOrderCommandHandler, PayCommandHandler pay) {
        this.bank = bank;
        this.transformToOrderCommandHandler = transformToOrderCommandHandler;
        this.pay = pay;
    }

    public CommandResponse<Event> handle(PayAndTransformToOrderCommand command) {
        var response = pay.handle(new PayCommand(command.cartId(), command.cardNumber(), command.expirationDate(), command.cypher(), command.amount()));

        var transaction = new Transaction("id", null, null);
        if (response.first() instanceof ValidationRequested validationRequested) {
            //TODO: replace payAndTransformToOrderResult by a ValidationRequested event
            var pendingTransaction = PayAndTransformToOrderResult.pending(
                    transaction.id(),
                    transaction.redirectionUrl(),
                    command.amount());
            LOGGER.info("Transaction is pending: {}", pendingTransaction);
            return null;
        }

        if (response.first() instanceof TransactionFailed transactionFailed) {
            //TODO: replace payAndTransformToOrderResult by a TransactionFailed event
            //TODO: then remove the PayAndTransformToOrderResult record
            var failedTransaction = PayAndTransformToOrderResult.failed(
                    transaction.id());
            LOGGER.info("Transaction failed: {}", failedTransaction);
            return null;
        }

        String transactionId = response.firstAs(PaymentSucceeded.class).transactionId();
        LOGGER.info("Transaction for cart id {} succeeded, with transaction id:{}", command.cartId(), transactionId);

        return transformToOrderCommandHandler.handle(new TransformToOrderCommand(transactionId, command.cartId(), command.amount()));
    }

    @Override
    public Class listenTo() {
        return PayAndTransformToOrderCommand.class;
    }
}
