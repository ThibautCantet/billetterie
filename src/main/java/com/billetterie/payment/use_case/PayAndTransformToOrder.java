package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.billetterie.payment.domain.PaymentStatus.*;

@Service
public class PayAndTransformToOrder implements CommandHandler<PayAndTransformToOrderCommand, CommandResponse<Event>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayAndTransformToOrder.class);

    private final TransformToOrder transformToOrder;
    private final Pay pay;

    public PayAndTransformToOrder(TransformToOrder transformToOrder, Pay pay) {
        this.transformToOrder = transformToOrder;
        this.pay = pay;
    }

    public CommandResponse<Event> execute(PayAndTransformToOrderCommand command) {
        Transaction transaction = pay.execute(new PayCommand(command.cartId(), command.cardNumber(), command.expirationDate(), command.cypher(), command.amount()));

        if (transaction.isPending()) {
            var pendingTransaction = new PayAndTransformToOrderResult(
                    PENDING,
                    transaction.id(),
                    transaction.redirectionUrl(),
                    null,
                    command.amount());
            LOGGER.info("Transaction is pending: {}", pendingTransaction);
            return null;
        }

        if (!transaction.hasSucceeded()) {
            var failedTransaction = new PayAndTransformToOrderResult(
                    transaction.status(),
                    transaction.id(),
                    null,
                    null,
                    0f);
            LOGGER.info("Transaction failed: {}", failedTransaction);
            return null;
        }

        LOGGER.info("Transaction for cart id {} succeeded, with transaction id:{}", command.cartId(), transaction.id());

        var response = transformToOrder.execute(new TransformToOrderCommand(transaction.id(), command.cartId(), command.amount()));

        return null;
    }

    @Override
    public Class listenTo() {
        return null;
    }
}
