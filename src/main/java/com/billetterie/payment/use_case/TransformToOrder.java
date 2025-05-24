package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.CartType;
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
    private final CancelTransaction cancelTransaction;
    private final AlertTransactionFailureCommandHandler alertTransactionFailureCommandHandler;

    public TransformToOrder(Orders orders, Bank bank, CustomerSupport customerSupport, CancelTransaction cancelTransaction, AlertTransactionFailureCommandHandler alertTransactionFailureCommandHandler) {
        this.orders = orders;
        this.bank = bank;
        this.customerSupport = customerSupport;
        this.cancelTransaction = cancelTransaction;
        this.alertTransactionFailureCommandHandler = alertTransactionFailureCommandHandler;
    }

    public PayAndTransformToOrderResult execute(TransformToOrderCommand command) {
        Order order = orders.transformToOrder(command.cartId(), command.amount());

        if (order.isNotCompleted()) {
            LOGGER.warn("Cart not transformed to order: {}", command.cartId());
            boolean cancel = cancelTransaction.execute(new CancelTransactionCommand(command.transactionId(), command.amount()));
            if (!cancel) {
                LOGGER.error("Transaction cancellation failed: {}", command.transactionId());
                alertTransactionFailureCommandHandler.handle(new AlertTransactionFailureCommand(command.transactionId(), command.cartId(), command.amount()));
            } else {
                LOGGER.info("Transaction cancelled: {}", command.transactionId());
            }

            String errorUrl;
            if (command.cartType() == CartType.CLASSIC) {
                errorUrl = getErrorCartUrl(command.cartId(), command.amount());
                LOGGER.info("Cart not transformed into order and redirect to empty cart: {}", command.cartId());
            } else {
                errorUrl = getErrorUrl(command.cartId(), command.amount());
                LOGGER.info("Panier reservé not transformed into order and redirect error: {}", command.cartId());
            }

            return PayAndTransformToOrderResult.failed(
                    command.transactionId(),
                    errorUrl);
        }

        String url;
        if (command.cartType() == CartType.CLASSIC) {
            url = String.format("/confirmation/%s?amount=%s", order.id(), command.amount());
            LOGGER.info("Cart transformed to order: {}", order.id());
        } else {
            url = String.format("/my-orders?id=%s&amount=%s", order.id(), command.amount());
            LOGGER.info("Panier réservé transformed to order: {}", order.id());
        }
        return PayAndTransformToOrderResult.succeeded(
                command.transactionId(),
                order.id(),
                command.amount(),
                command.cartType(),
                url);
    }

    public static String getErrorCartUrl(String cartId, float amount) {
        return "/cart?error=true&cartId=" + cartId + "&amount=" + amount;
    }

    public static String getErrorUrl(String cartId, float amount) {
        return "/panier-reserve-error?cartId=" + cartId + "&amount=" + amount;
    }
}
