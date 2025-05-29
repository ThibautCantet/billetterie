package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.PaymentStatus;
import com.billetterie.payment.domain.Transaction;
import com.billetterie.payment.domain.TransactionFailed;
import com.billetterie.payment.domain.ValidationRequested;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PayAndTransformToOrder implements CommandHandler<PayAndTransformToOrderCommand, CommandResponse<Event>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayAndTransformToOrder.class);

    private final Bank bank;
    private final TransformToOrder transformToOrder;
    private final Pay pay;

    public PayAndTransformToOrder(Bank bank, TransformToOrder transformToOrder, Pay pay) {
        this.bank = bank;
        this.transformToOrder = transformToOrder;
        this.pay = pay;
    }

    public CommandResponse<Event> execute(PayAndTransformToOrderCommand command) {
        Transaction transaction = pay.execute(new PayCommand(command.cartId(), command.cardNumber(), command.expirationDate(), command.cypher(), command.amount()));

        if (transaction.isPending()) {
            var validationRequested = new ValidationRequested(
                    PaymentStatus.PENDING,
                    transaction.id(),
                    transaction.redirectionUrl(),
                    command.amount());
            LOGGER.info("Transaction is pending: {}", validationRequested);
            return new CommandResponse<>(validationRequested);
        }

        if (!transaction.hasSucceeded()) {
            var transactionFailed = new TransactionFailed(
                    transaction.status(),
                    transaction.id());
            LOGGER.info("Transaction failed: {}", transactionFailed);
            return  new CommandResponse<>(transactionFailed);
        }

        LOGGER.info("Transaction for cart id {} succeeded, with transaction id:{}", command.cartId(), transaction.id());

        return transformToOrder.execute(new TransformToOrderCommand(transaction.id(), command.cartId(), command.amount()));
    }

    @Override
    public Class listenTo() {
        return null;
    }
}
