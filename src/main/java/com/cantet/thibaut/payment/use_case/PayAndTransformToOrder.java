package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.domain.Bank;
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

    public PayAndTransformToOrder(Bank bank, Orders orders) {
        this.bank = bank;
        this.orders = orders;
    }

    public PayAndTransformToOrderResult execute(String cartId, String cardNumber, String expirationDate, String cypher, float amount) {
        Transaction transaction = bank.pay(new Payment(cardNumber, expirationDate, cypher, amount));

        if (transaction.status() != SUCCESS) {
            return new PayAndTransformToOrderResult(
                    transaction.status(),
                    transaction.id(),
                    null,
                    null,
                    0);
        }

        Order order = orders.transformToOrder(cartId, amount);

        if (order.isNotCompleted()) {
            return new PayAndTransformToOrderResult(
                    FAILED,
                    transaction.id(),
                    null,
                    null,
                    0);
        }

        return new PayAndTransformToOrderResult(
                SUCCESS,
                transaction.id(),
                "confirmation",
                order.id(),
                order.amount());
    }
}
