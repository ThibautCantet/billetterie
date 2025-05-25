package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.CancelTransactionFailed;
import com.billetterie.payment.domain.Order;
import com.billetterie.payment.domain.Orders;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransformToOrder implements CommandHandler<TransformToOrderCommand, CommandResponse<Event>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformToOrder.class);

    private final Orders orders;
    private final CancelTransaction cancelTransaction;
    private final AlertTransactionFailure alertTransactionFailure;

    public TransformToOrder(Orders orders, CancelTransaction cancelTransaction, AlertTransactionFailure alertTransactionFailure) {
        this.orders = orders;
        this.cancelTransaction = cancelTransaction;
        this.alertTransactionFailure = alertTransactionFailure;
    }

    public CommandResponse<Event> execute(TransformToOrderCommand command) {
        Order order = orders.transformToOrder(command.cartId(), command.amount());

        if (order.isNotCompleted()) {
            LOGGER.warn("Cart not transformed to order: {}", command.cartId());
            var cancel = cancelTransaction.execute(new CancelTransactionCommand(command.transactionId(), command.cartId(), command.amount()));
            if (cancel.first() instanceof CancelTransactionFailed) {
                LOGGER.error("Transaction cancellation failed: {}", command.transactionId());
                alertTransactionFailure.execute(new AlertTransactionFailureCommand(command.transactionId(), command.cartId(), command.amount()));
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

            return null;
        }

        LOGGER.info("Cart transformed to order: {}", order.id());
        new PayAndTransformToOrderResult(
                PaymentStatus.SUCCESS,
                command.transactionId(),
                String.format("/confirmation/%s?amount=%s", order.id(), command.amount()),
                order.id(),
                command.amount());

        return null;
    }

    @Override
    public Class listenTo() {
        return null;
    }

    public static String getErrorCartUrl(String cartId, float amount) {
        return "/cart?error=true&cartId=" + cartId + "&amount=" + amount;
    }
}
