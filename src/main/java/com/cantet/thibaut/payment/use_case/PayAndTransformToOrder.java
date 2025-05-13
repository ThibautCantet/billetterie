package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.domain.Bank;
import com.cantet.thibaut.payment.domain.CustomerSupport;
import com.cantet.thibaut.payment.domain.Order;
import com.cantet.thibaut.payment.domain.Orders;
import com.cantet.thibaut.payment.domain.Payment;
import com.cantet.thibaut.payment.domain.PayAndTransformToOrderResult;
import com.cantet.thibaut.payment.domain.Transaction;
import org.springframework.stereotype.Service;

import static com.cantet.thibaut.payment.domain.PaymentStatus.*;

@Service
public class PayAndTransformToOrder {
    private final Bank bank;
    private final Orders orders;
    private final CustomerSupport customerSupport;

    public PayAndTransformToOrder(Bank bank, Orders orders, CustomerSupport customerSupport) {
        this.bank = bank;
        this.orders = orders;
        this.customerSupport = customerSupport;
    }

    public PayAndTransformToOrderResult execute(String cartId, String cardNumber, String expirationDate, String cypher, float amount) {
        Transaction transaction = bank.pay(new Payment(cardNumber, expirationDate, cypher, amount));

        if (transaction.isPending()) {
            return new PayAndTransformToOrderResult(
                    PENDING,
                    transaction.id(),
                    transaction.redirectionUrl(),
                    null,
                    amount);
        }

        if (!transaction.hasSucceeded()) {
            return new PayAndTransformToOrderResult(
                    transaction.status(),
                    transaction.id(),
                    null,
                    null,
                    0);
        }

        Order order = orders.transformToOrder(cartId, amount);

        if (order.isNotCompleted()) {
            boolean cancel = bank.cancel(transaction.id());
            if (!cancel) {
                customerSupport.alertTransactionFailure(transaction.id(), cartId, amount);
            }

            return new PayAndTransformToOrderResult(
                    FAILED,
                    transaction.id(),
                    "/panier",
                    null,
                    0);
        }

        return new PayAndTransformToOrderResult(
                SUCCESS,
                transaction.id(),
                String.format("/confirmation/%s?amount=%s", order.id(), amount),
                order.id(),
                order.amount());
    }
}
