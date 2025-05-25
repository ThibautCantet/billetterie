package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PayAndTransformToOrder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayAndTransformToOrder.class);

    private final Bank bank;
    private final TransformToOrderCommandHandler transformToOrderCommandHandler;
    private final Pay pay;

    public PayAndTransformToOrder(Bank bank, TransformToOrderCommandHandler transformToOrderCommandHandler, Pay pay) {
        this.bank = bank;
        this.transformToOrderCommandHandler = transformToOrderCommandHandler;
        this.pay = pay;
    }

    public PayAndTransformToOrderResult execute(PayAndTransformToOrderCommand command) {
        Transaction transaction = pay.execute(new PayCommand(command.cartId(), command.cardNumber(), command.expirationDate(), command.cypher(), command.amount()));

        if (transaction.isPending()) {
            var pendingTransaction = PayAndTransformToOrderResult.pending(
                    transaction.id(),
                    transaction.redirectionUrl(),
                    command.amount());
            LOGGER.info("Transaction is pending: {}", pendingTransaction);
            return pendingTransaction;
        }

        if (!transaction.hasSucceeded()) {
            var failedTransaction = PayAndTransformToOrderResult.failed(
                    transaction.id());
            LOGGER.info("Transaction failed: {}", failedTransaction);
            return failedTransaction;
        }

        LOGGER.info("Transaction for cart id {} succeeded, with transaction id:{}", command.cartId(), transaction.id());

        var response = transformToOrderCommandHandler.handle(new TransformToOrderCommand(transaction.id(), command.cartId(), command.amount()));

        return null;
    }
}
