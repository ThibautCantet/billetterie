package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.domain.Bank;
import com.cantet.thibaut.payment.domain.CustomerSupport;
import com.cantet.thibaut.payment.domain.Order;
import com.cantet.thibaut.payment.domain.Orders;
import com.cantet.thibaut.payment.domain.PayAndTransformToOrderResult;
import com.cantet.thibaut.payment.domain.PaymentStatus;
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

    public PayAndTransformToOrderResult execute(TransformToOrderCommand command) {
        Order order = orders.transformToOrder(command.cartId(), command.amount());

        if (order.isNotCompleted()) {
            LOGGER.warn("Cart not transformed to order: {}", command.cartId());
            boolean cancel = bank.cancel(command.transactionId(), command.amount());
            if (!cancel) {
                LOGGER.error("Transaction cancellation failed: {}", command.transactionId());
                customerSupport.alertTransactionFailure(command.transactionId(), command.cartId(), command.amount());
            } else {
                LOGGER.info("Transaction cancelled: {}", command.transactionId());
            }

            var payAndTransformToOrderResult = new PayAndTransformToOrderResult(
                    PaymentStatus.FAILED,
                    command.transactionId(),
                    getErrorCartUrl(command.cartId(), command.amount()),
                    null,
                    null);
            LOGGER.info("Cart not transformed into order and redirect to empty cart: {}", payAndTransformToOrderResult);

            return payAndTransformToOrderResult;
        }

        LOGGER.info("Cart transformed to order: {}", order.id());
        return new PayAndTransformToOrderResult(
                PaymentStatus.SUCCESS,
                command.transactionId(),
                String.format("/confirmation/%s?amount=%s", order.id(), command.amount()),
                order.id(),
                command.amount());
    }

    public static String getErrorCartUrl(String cartId, float amount) {
        return "/cart?error=true&cartId=" + cartId + "&amount=" + amount;
    }
}
