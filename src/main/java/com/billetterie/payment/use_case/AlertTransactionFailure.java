package com.billetterie.payment.use_case;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.CustomerSupport;
import com.billetterie.payment.domain.CustomerSupportAlerted;
import org.springframework.stereotype.Service;

@Service
public class AlertTransactionFailure implements CommandHandler<AlertTransactionFailureCommand, CommandResponse<Event>> {
    private final CustomerSupport customerSupport;

    public AlertTransactionFailure(CustomerSupport customerSupport) {
        this.customerSupport = customerSupport;
    }

    public CommandResponse<Event> execute(AlertTransactionFailureCommand command) {
        customerSupport.alertTransactionFailure(command.transactionId(), command.cartId(), command.amount());
        return new CommandResponse<>(new CustomerSupportAlerted());
    }

    @Override
    public Class listenTo() {
        return AlertTransactionFailureCommand.class;
    }
}
