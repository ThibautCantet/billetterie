package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.domain.Bank;
import com.cantet.thibaut.payment.domain.CustomerSupport;
import com.cantet.thibaut.payment.domain.Order;
import com.cantet.thibaut.payment.domain.Orders;
import com.cantet.thibaut.payment.domain.TransformToOrderResult;
import com.cantet.thibaut.payment.domain.TransformToOrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransformToOrder {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformToOrder.class);

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
            LOGGER.info("Cart not transformed to order: {}", cartId);
            boolean cancel = bank.cancel(transactionId, amount);
            if (!cancel) {
                LOGGER.info("Transaction cancellation failed: {}", transactionId);
                customerSupport.alertTransactionFailure(transactionId, cartId, amount);
            }

            return new TransformToOrderResult(
                    TransformToOrderStatus.FAILED,
                    transactionId,
                    "/panier",
                    null,
                    null);
        }

        LOGGER.info("Cart transformed to order: {}", order.id());
        return new TransformToOrderResult(
                TransformToOrderStatus.SUCCEEDED,
                transactionId,
                String.format("/confirmation/%s?amount=%s", order.id(), amount),
                order.id(),
                amount);
    }
}
