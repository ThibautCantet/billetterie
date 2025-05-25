package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.common.cqrs.command.CommandHandler;
import com.cantet.thibaut.payment.common.cqrs.command.CommandResponse;
import com.cantet.thibaut.payment.common.cqrs.event.Event;
import com.cantet.thibaut.payment.domain.Transaction;
import com.cantet.thibaut.payment.domain.TransactionFailed;
import com.cantet.thibaut.payment.domain.ValidationRequested;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.cantet.thibaut.payment.domain.PaymentStatus.*;

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
        Transaction transaction = pay.execute(new PayCommand(command.cartId(), command.cardNumber(), command.expirationDate(), command.cypher(), command.amount()));

        if (transaction.isPending()) {
            var validationRequested = new ValidationRequested(
                    PENDING,
                    transaction.id(),
                    transaction.redirectionUrl(),
                    command.amount());
            LOGGER.info("Transaction is pending: {}", validationRequested);
            return new CommandResponse<>(validationRequested);
        }

        if (!transaction.hasSucceeded()) {
            var failedTransaction = new TransactionFailed(
                    transaction.status(),
                    transaction.id());
            LOGGER.info("Transaction failed: {}", failedTransaction);
            return new CommandResponse<>(failedTransaction);
        }

        LOGGER.info("Transaction for cart transactionId {} succeeded, with transaction transactionId:{}", command.cartId(), transaction.id());

        return transformToOrder.execute(new TransformToOrderCommand(transaction.id(), command.cartId(), command.amount()));
    }

    @Override
    public Class listenTo() {
        return PayAndTransformToOrderCommand.class;
    }
}
