package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.CustomerSupport;
import com.billetterie.payment.domain.Order;
import com.billetterie.payment.domain.Orders;
import com.billetterie.payment.domain.Payment;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.billetterie.payment.domain.PaymentStatus.*;
import static com.billetterie.payment.use_case.TransformToOrder.*;

@Service
public class PayAndTransformToOrder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayAndTransformToOrder.class);

    private final Bank bank;
    private final Orders orders;
    private final CustomerSupport customerSupport;

    public PayAndTransformToOrder(Bank bank, Orders orders, CustomerSupport customerSupport) {
        this.bank = bank;
        this.orders = orders;
        this.customerSupport = customerSupport;
    }

    public PayAndTransformToOrderResult execute(String cartId, String cardNumber, String expirationDate, String cypher, float amount) {
        Transaction transaction = bank.pay(new Payment(cardNumber, expirationDate, cypher, cartId, amount));

        if (transaction.isPending()) {
            var pendingTransaction = new PayAndTransformToOrderResult(
                    PENDING,
                    transaction.id(),
                    transaction.redirectionUrl(),
                    null,
                    amount);
            LOGGER.info("Transaction is pending: {}", pendingTransaction);
            return pendingTransaction;
        }

        if (!transaction.hasSucceeded()) {
            var failedTransaction = new PayAndTransformToOrderResult(
                    transaction.status(),
                    transaction.id(),
                    null,
                    null,
                    0);
            LOGGER.info("Transaction failed: {}", failedTransaction);
            return failedTransaction;
        }

        LOGGER.info("Transaction for cart id {} succeeded, with transaction id:{}", cartId, transaction.id());

        Order order = orders.transformToOrder(cartId, amount);

        if (order.isNotCompleted()) {
            LOGGER.warn("Cart not transformed to order: {}", cartId);
            boolean cancel = bank.cancel(transaction.id(), amount);
            if (!cancel) {
                LOGGER.error("Transaction cancellation failed: {}", transaction.id());
                customerSupport.alertTransactionFailure(transaction.id(), cartId, amount);
            } else {
                LOGGER.info("Transaction cancelled: {}", transaction.id());
            }

            var payAndTransformToOrderResult = new PayAndTransformToOrderResult(
                    FAILED,
                    transaction.id(),
                    getErrorCartUrl(cartId, amount),
                    null,
                    0);

            LOGGER.info("Cart not transformed into order and redirect to empty cart: {}", payAndTransformToOrderResult);
            return payAndTransformToOrderResult;
        }

        LOGGER.info("Cart transformed to order: {}", order.id());
        return new PayAndTransformToOrderResult(
                SUCCESS,
                transaction.id(),
                String.format("/confirmation/%s?amount=%s", order.id(), amount),
                order.id(),
                order.amount());
    }
}
