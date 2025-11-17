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

    public TransformToOrder(Orders orders, Bank bank, CustomerSupport customerSupport) {
        this.orders = orders;
        this.bank = bank;
        this.customerSupport = customerSupport;
    }

    public PayAndTransformToOrderResult execute(String transactionId, String cartId, float amount, CartType type) {
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

            String errorUrl;
            if (type == CartType.CLASSIC) {
                errorUrl = getErrorCartUrl(cartId, amount);
                LOGGER.info("Cart not transformed into order and redirect to empty cart: {}", cartId);
            } else {
                errorUrl = getErrorUrl(cartId, amount);
                LOGGER.info("Panier reservé not transformed into order and redirect error: {}", cartId);
            }

            return PayAndTransformToOrderResult.failed(
                    transactionId,
                    errorUrl);
        }

        String url;
        if (type == CartType.CLASSIC) {
            url = String.format("/confirmation/%s?amount=%s", order.id(), amount);
            LOGGER.info("Cart transformed to order: {}", order.id());
        } else {
            url = String.format("/my-orders?id=%s&amount=%s", order.id(), amount);
            LOGGER.info("Panier réservé transformed to order: {}", order.id());
        }
        return PayAndTransformToOrderResult.succeeded(
                transactionId,
                order.id(),
                amount,
                type,
                url);
    }

    public static String getErrorCartUrl(String cartId, float amount) {
        return "/cart?error=true&cartId=" + cartId + "&amount=" + amount;
    }

    public static String getErrorUrl(String cartId, float amount) {
        return "/panier-reserve-error?cartId=" + cartId + "&amount=" + amount;
    }
}
