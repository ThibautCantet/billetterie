package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.Payment;
import com.billetterie.payment.domain.Transaction;
import com.billetterie.payment.payment.use_case.PayAndTransformToOrderCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.billetterie.payment.domain.PaymentStatus.*;

@Service
public class PayAndTransformToOrder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayAndTransformToOrder.class);

    private final Bank bank;
    private final TransformToOrder transformToOrder;

    public PayAndTransformToOrder(Bank bank, TransformToOrder transformToOrder) {
        this.bank = bank;
        this.transformToOrder = transformToOrder;
    }

    public PayAndTransformToOrderResult execute(PayAndTransformToOrderCommand command) {
        Transaction transaction = bank.pay(new Payment(command.cardNumber(), command.expirationDate(), command.cypher(), command.cartId(), command.amount()));

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

        return transformToOrder.execute(transaction.id(), command.cartId(), command.amount());
    }
}
