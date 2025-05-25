package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PayAndTransformToOrder implements CommandHandler<PayAndTransformToOrderCommand, CommandResponse<Event>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayAndTransformToOrder.class);

    private final Bank bank;
    private final TransformToOrder transformToOrder;
    private final Pay pay;

    public PayAndTransformToOrder(Bank bank, TransformToOrder transformToOrder, Pay pay) {
        this.bank = bank;
        this.transformToOrder = transformToOrder;
        this.pay = pay;
    }

    public CommandResponse<Event> execute(PayAndTransformToOrderCommand command) {
        Transaction transaction = pay.execute(new PayCommand(command.cartId(), command.cardNumber(), command.expirationDate(), command.cypher(), command.amount()));

        if (transaction.isPending()) {
            //TODO: replace payAndTransformToOrderResult by a ValidationRequested event
            var pendingTransaction = PayAndTransformToOrderResult.pending(
                    transaction.id(),
                    transaction.redirectionUrl(),
                    command.amount());
            LOGGER.info("Transaction is pending: {}", pendingTransaction);
            return null;
        }

        if (!transaction.hasSucceeded()) {
            //TODO: replace payAndTransformToOrderResult by a TransactionFailed event
            //TODO: then remove the PayAndTransformToOrderResult record
            var failedTransaction = PayAndTransformToOrderResult.failed(
                    transaction.status(),
                    transaction.id());
            LOGGER.info("Transaction failed: {}", failedTransaction);
            return null;
        }

        LOGGER.info("Transaction for cart id {} succeeded, with transaction id:{}", command.cartId(), transaction.id());

        //TODO: return the result of the transformation to order
        var response = transformToOrder.execute(new TransformToOrderCommand(transaction.id(), command.cartId(), command.amount()));

        return null;
    }

    @Override
    public Class listenTo() {
        return null;
    }
}
