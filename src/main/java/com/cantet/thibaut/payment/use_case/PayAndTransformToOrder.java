package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.domain.PayAndTransformToOrderResult;
import com.cantet.thibaut.payment.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.cantet.thibaut.payment.domain.PaymentStatus.*;

@Service
public class PayAndTransformToOrder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayAndTransformToOrder.class);

    private final TransformToOrder transformToOrder;
    private final Pay pay;

    public PayAndTransformToOrder(TransformToOrder transformToOrder, Pay pay) {
        this.transformToOrder = transformToOrder;
        this.pay = pay;
    }

    public PayAndTransformToOrderResult execute(PayAndTransformToOrderCommand command) {
        Transaction transaction = pay.execute(new PayCommand(command.cartId(), command.cardNumber(), command.expirationDate(), command.cypher(), command.amount()));

        if (transaction.isPending()) {
            var pendingTransaction = new PayAndTransformToOrderResult(
                    PENDING,
                    transaction.id(),
                    transaction.redirectionUrl(),
                    null,
                    command.amount());
            LOGGER.info("Transaction is pending: {}", pendingTransaction);
            return pendingTransaction;
        }

        if (!transaction.hasSucceeded()) {
            var failedTransaction = new PayAndTransformToOrderResult(
                    transaction.status(),
                    transaction.id(),
                    null,
                    null,
                    0f);
            LOGGER.info("Transaction failed: {}", failedTransaction);
            return failedTransaction;
        }

        LOGGER.info("Transaction for cart id {} succeeded, with transaction id:{}", command.cartId(), transaction.id());

        return transformToOrder.execute(new TransformToOrderCommand(transaction.id(), command.cartId(), command.amount()));
    }
}
