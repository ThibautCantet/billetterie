package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.CustomerSupport;
import com.billetterie.payment.domain.Order;
import com.billetterie.payment.domain.Orders;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.PaymentStatus;
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

    public PayAndTransformToOrderResult execute(String transactionId, String cartId, float amount) {
        Order order = orders.transformToOrder(cartId, amount);

        if (order.isNotCompleted()) {
            LOGGER.warn("Cart not transformed to order: {}", cartId);
            boolean cancel = bank.cancel(transactionId, amount);
            if (!cancel) {
                LOGGER.error("Transaction cancellation failed: {}", transactionId);
                customerSupport.alertTransactionFailure(transactionId, cartId, amount);
            } else {
                LOGGER.info("Transaction cancelled: {}", transactionId);
            }

            var payAndTransformToOrderResult = PayAndTransformToOrderResult.failed(
                    PaymentStatus.FAILED,
                    transactionId,
                    getErrorCartUrl(cartId, amount));
            LOGGER.info("Cart not transformed into order and redirect to empty cart: {}", payAndTransformToOrderResult);

            return payAndTransformToOrderResult;
        }

        LOGGER.info("Cart transformed to order: {}", order.id());
        return PayAndTransformToOrderResult.succeeded(
                transactionId,
                order.id(),
                amount);
    }

    public static String getErrorCartUrl(String cartId, float amount) {
        return "/cart?error=true&cartId=" + cartId + "&amount=" + amount;
    }
}
