package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.CancelTransactionFailed;
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
public class TransformToOrderCommandHandler implements CommandHandler<TransformToOrderCommand, CommandResponse<Event>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformToOrderCommandHandler.class);

    private final Orders orders;
    private final Bank bank;
    private final CustomerSupport customerSupport;
    private final CancelTransactionCommandHandler cancelTransactionCommandHandler;
    private final AlertTransactionFailureHandler alertTransactionFailureHandler;

    public TransformToOrderCommandHandler(Orders orders, Bank bank, CustomerSupport customerSupport, CancelTransactionCommandHandler cancelTransactionCommandHandler, AlertTransactionFailureHandler alertTransactionFailureHandler) {
        this.orders = orders;
        this.bank = bank;
        this.customerSupport = customerSupport;
        this.cancelTransactionCommandHandler = cancelTransactionCommandHandler;
        this.alertTransactionFailureHandler = alertTransactionFailureHandler;
    }

    public CommandResponse<Event> handle(TransformToOrderCommand command) {
        Order order = orders.transformToOrder(command.cartId(), command.amount());

        if (order.isNotCompleted()) {
            LOGGER.warn("Cart not transformed to order: {}", command.cartId());
            var cancel = cancelTransactionCommandHandler.handle(new CancelTransactionCommand(command.transactionId(), command.cartId(), command.amount()));
            if (cancel.first() instanceof CancelTransactionFailed) {
                LOGGER.error("Transaction cancellation failed: {}", command.transactionId());
                alertTransactionFailureHandler.handle(new AlertTransactionFailureCommand(command.transactionId(), command.cartId(), command.amount()));
            } else {
                LOGGER.info("Transaction cancelled: {}", command.transactionId());
            }

            //TODO: replace payAndTransformToOrderResult by a OrderNotCreated event
            var payAndTransformToOrderResult = PayAndTransformToOrderResult.failed(
                    PaymentStatus.FAILED,
                    command.transactionId(),
                    getErrorCartUrl(command.cartId(), command.amount()));
            LOGGER.info("Cart not transformed into order and redirect to empty cart: {}", payAndTransformToOrderResult);

            return null;
        }

        LOGGER.info("Cart transformed to order: {}", order.id());
        //TODO: replace payAndTransformToOrderResult by a OrderCreated event
        //TODO: use OrderCreated.of
        PayAndTransformToOrderResult.succeeded(
                command.transactionId(),
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
