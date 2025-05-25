package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.PaymentStatus;
import com.billetterie.payment.domain.PaymentSucceeded;
import com.billetterie.payment.domain.Transaction;
import com.billetterie.payment.domain.TransactionFailed;
import com.billetterie.payment.domain.ValidationRequested;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PayAndTransformToOrder implements CommandHandler<PayAndTransformToOrderCommand, CommandResponse<Event>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayAndTransformToOrder.class);

    private final TransformToOrder transformToOrder;
    private final Pay pay;

    public PayAndTransformToOrder(TransformToOrder transformToOrder, Pay pay) {
        this.transformToOrder = transformToOrder;
        this.pay = pay;
    }

    public CommandResponse<Event> execute(PayAndTransformToOrderCommand command) {
        var response = pay.execute(new PayCommand(command.cartId(), command.cardNumber(), command.expirationDate(), command.cypher(), command.amount()));

        if (response.first() instanceof ValidationRequested validationRequested) {
            LOGGER.info("Transaction is pending: {}", validationRequested);
            return new CommandResponse<>(validationRequested);
        }

        if (response.first() instanceof TransactionFailed transactionFailed) {
            LOGGER.info("Transaction failed: {}", transactionFailed);
            return new CommandResponse<>(transactionFailed);
        }

        String transactionId = response.firstAs(PaymentSucceeded.class).transactionId();
        LOGGER.info("Transaction for cart id {} succeeded, with transaction id:{}", command.cartId(), transactionId);

        return transformToOrder.execute(new TransformToOrderCommand(transactionId, command.cartId(), command.amount()));
    }

    @Override
    public Class listenTo() {
        return PayAndTransformToOrderCommand.class;
    }
}
