package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.domain.Bank;
import com.cantet.thibaut.payment.domain.CustomerSupport;
import com.cantet.thibaut.payment.domain.Order;
import com.cantet.thibaut.payment.domain.Orders;
import com.cantet.thibaut.payment.domain.TransformToOrderResult;
import com.cantet.thibaut.payment.domain.TransformToOrderStatus;
import org.springframework.stereotype.Service;

@Service
public class TransformToOrder {
    private final Orders orders;
    private final Bank bank;
    private final CustomerSupport customerSupport;

    public TransformToOrder(Orders orders, Bank bank, CustomerSupport customerSupport) {
        this.orders = orders;
        this.bank = bank;
        this.customerSupport = customerSupport;
    }

    public TransformToOrderResult execute(String transactionId, String cartId, float amount) {
        Order order = orders.transformToOrder(cartId, amount);

        if (order.isNotCompleted()) {
            boolean cancel = bank.cancel(transactionId);
            if (!cancel) {
                customerSupport.alertTransactionFailure(transactionId, cartId, amount);
            }

            return new TransformToOrderResult(
                    TransformToOrderStatus.FAILED,
                    transactionId,
                    "/panier",
                    null,
                    null);
        }

        return new TransformToOrderResult(
                TransformToOrderStatus.SUCCEEDED,
                transactionId,
                String.format("/confirmation/%s?amount=%s", order.id(), amount),
                order.id(),
                amount);
    }
}
