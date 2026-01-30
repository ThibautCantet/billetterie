package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.ConfirmationService;
import com.billetterie.payment.domain.CustomerSupport;
import com.billetterie.payment.domain.Order;
import com.billetterie.payment.domain.Orders;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransformToOrderCommandHandler implements CommandHandler<TransformToOrderCommand, CommandResponse<Event>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformToOrderCommandHandler.class);

    private final Orders orders;
    private final Bank bank;
    private final CustomerSupport customerSupport;
    private final ConfirmationService confirmationService;

    public TransformToOrderCommandHandler(Orders orders, Bank bank, CustomerSupport customerSupport, ConfirmationService confirmationService) {
        this.orders = orders;
        this.bank = bank;
        this.customerSupport = customerSupport;
        this.confirmationService = confirmationService;
    }

    public PayAndTransformToOrderResult handle(String transactionId, String cartId, float amount, String email) {
        Order order = orders.transformToOrder(cartId, amount);

        if (order.isNotCompleted()) {
            LOGGER.warn("Cart not transformed to order: {}", cartId);
            //TODO(4): replacer avec un nouveau use case CancelTransaction
            boolean cancel = bank.cancel(transactionId, amount);
            if (!cancel) {
                LOGGER.error("Transaction cancellation failed: {}", transactionId);
                //TODO(5): remplacer avec un nouveau use case AlertTransactionFailure
                customerSupport.alertTransactionFailure(transactionId, cartId, amount);
            } else {
                LOGGER.info("Transaction cancelled: {}", transactionId);
            }

            //TODO: replace payAndTransformToOrderResult by a OrderNotCreated event
            var failed = PayAndTransformToOrderResult.failed(
                    transactionId,
                    getErrorCartUrl(cartId, amount));
            LOGGER.info("Cart not transformed into order and redirect to empty cart: {}", failed);

            return failed;
        }

        confirmationService.send(email, order.id(), order.amount());

        LOGGER.info("Cart transformed to order: {}", order.id());
        //TODO: replace payAndTransformToOrderResult by a OrderCreated event
        //TODO: use OrderCreated.of
        PayAndTransformToOrderResult.succeeded(
                transactionId,
                order.id(),
                amount);

        return null;
    }

    @Override
    public CommandResponse<Event> handle(TransformToOrderCommand command) {
        return null;
    }

    @Override
    public Class listenTo() {
        return TransformToOrderCommand.class;
    }

    public static String getErrorCartUrl(String cartId, float amount) {
        return "/cart?error=true&cartId=" + cartId + "&amount=" + amount;
    }
}
