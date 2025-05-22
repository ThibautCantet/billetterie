package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.Payment;
import com.billetterie.payment.domain.Transaction;
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

    public PayAndTransformToOrderResult execute(String cartId, String cardNumber, String expirationDate, String cypher, float amount) {
        Transaction transaction = bank.pay(new Payment(cardNumber, expirationDate, cypher, cartId, amount));

        if (transaction.isPending()) {
            var pendingTransaction = PayAndTransformToOrderResult.pending(
                    transaction.id(),
                    transaction.redirectionUrl(),
                    amount);
            LOGGER.info("Transaction is pending: {}", pendingTransaction);
            return pendingTransaction;
        }

        if (!transaction.hasSucceeded()) {
            var failedTransaction = PayAndTransformToOrderResult.failed(
                    transaction.id());
            LOGGER.info("Transaction failed: {}", failedTransaction);
            return failedTransaction;
        }

        LOGGER.info("Transaction for cart id {} succeeded, with transaction id:{}", cartId, transaction.id());

        return transformToOrder.execute(transaction.id(), cartId, amount);
    }
}
