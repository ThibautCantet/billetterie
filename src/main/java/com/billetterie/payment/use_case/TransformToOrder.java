package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.CancelTransactionFailed;
import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.CustomerSupport;
import com.billetterie.payment.domain.Order;
import com.billetterie.payment.domain.Orders;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransformToOrder {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformToOrder.class);

    private final Orders orders;
    private final Bank bank;
    private final CustomerSupport customerSupport;
    private final CancelTransactionCommandHandler cancelTransactionCommandHandler;
    private final AlertTransactionFailureCommandHandler alertTransactionFailureCommandHandler;

    public TransformToOrder(Orders orders, Bank bank, CustomerSupport customerSupport, CancelTransactionCommandHandler cancelTransactionCommandHandler, AlertTransactionFailureCommandHandler alertTransactionFailureCommandHandler) {
        this.orders = orders;
        this.bank = bank;
        this.customerSupport = customerSupport;
        this.cancelTransactionCommandHandler = cancelTransactionCommandHandler;
        this.alertTransactionFailureCommandHandler = alertTransactionFailureCommandHandler;
    }

    public PayAndTransformToOrderResult execute(TransformToOrderCommand command) {
        Order order = orders.transformToOrder(command.cartId(), command.amount());

        if (order.isNotCompleted()) {
            LOGGER.warn("Cart not transformed to order: {}", command.cartId());
            var cancel = cancelTransactionCommandHandler.handle(new CancelTransactionCommand(command.transactionId(), command.cartId(), command.amount()));
            if (cancel.first() instanceof CancelTransactionFailed) {
                LOGGER.error("Transaction cancellation failed: {}", command.transactionId());
                alertTransactionFailureCommandHandler.handle(new AlertTransactionFailureCommand(command.transactionId(), command.cartId(), command.amount()));
            } else {
                LOGGER.info("Transaction cancelled: {}", command.transactionId());
            }

            var failed = PayAndTransformToOrderResult.failed(
                    command.transactionId(),
                    getErrorCartUrl(command.cartId(), command.amount()));
            LOGGER.info("Cart not transformed into order and redirect to empty cart: {}", failed);

            return failed;
        }

        LOGGER.info("Cart transformed to order: {}", order.id());
        return PayAndTransformToOrderResult.succeeded(
                command.transactionId(),
                order.id(),
                command.amount());
    }

    public static String getErrorCartUrl(String cartId, float amount) {
        return "/cart?error=true&cartId=" + cartId + "&amount=" + amount;
    }
}
