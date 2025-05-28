package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.Payment;
import com.billetterie.payment.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PayAndTransformToOrder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayAndTransformToOrder.class);

    private final Bank bank;
    private final TransformToOrder transformToOrder;
    private final Pay pay;

    public PayAndTransformToOrder(Bank bank, TransformToOrder transformToOrder, Pay pay) {
        this.bank = bank;
        this.transformToOrder = transformToOrder;
        this.pay = pay;
    }

    public PayAndTransformToOrderResult execute(PayAndTransformToOrderCommand command) {
        Transaction transaction = bank.pay(new Payment(command.cartId(), command.cardNumber(), command.expirationDate(), command.cypher(), command.amount()));

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
                    transaction.status(),
                    transaction.id());
            LOGGER.info("Transaction failed: {}", failedTransaction);
            return failedTransaction;
        }

        LOGGER.info("Transaction for cart id {} succeeded, with transaction id:{}", command.cartId(), transaction.id());

        return transformToOrder.execute(new TransformToOrderCommand(transaction.id(), command.cartId(), command.amount()));
    }
}
