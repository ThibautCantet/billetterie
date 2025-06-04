package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.PaymentStatus;
import com.billetterie.payment.domain.Transaction;
import com.billetterie.payment.domain.TransactionFailed;
import com.billetterie.payment.domain.ValidationRequested;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PayAndTransformToOrderCommandHandler implements CommandHandler<PayAndTransformToOrderCommand, CommandResponse<Event>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayAndTransformToOrderCommandHandler.class);

    private final Bank bank;
    private final TransformToOrderCommandHandler transformToOrderCommandHandler;
    private final Pay pay;

    public PayAndTransformToOrderCommandHandler(Bank bank, TransformToOrderCommandHandler transformToOrderCommandHandler, Pay pay) {
        this.bank = bank;
        this.transformToOrderCommandHandler = transformToOrderCommandHandler;
        this.pay = pay;
    }

    public CommandResponse<Event> handle(PayAndTransformToOrderCommand command) {
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
                    PaymentStatus.FAILED,
                    transaction.id());
            LOGGER.info("Transaction failed: {}", transactionFailed);
            return new CommandResponse<>(transactionFailed);
        }

        LOGGER.info("Transaction for cart id {} succeeded, with transaction id:{}", command.cartId(), transaction.id());

        return transformToOrderCommandHandler.handle(new TransformToOrderCommand(transaction.id(), command.cartId(), command.amount()));
    }

    @Override
    public Class listenTo() {
        return PayAndTransformToOrderCommand.class;
    }
}
