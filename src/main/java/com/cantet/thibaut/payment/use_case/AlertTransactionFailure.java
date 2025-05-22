package com.cantet.thibaut.payment.use_case;

import java.util.List;

import com.cantet.thibaut.payment.common.cqrs.command.CommandHandler;
import com.cantet.thibaut.payment.common.cqrs.command.CommandResponse;
import com.cantet.thibaut.payment.common.cqrs.event.Event;
import com.cantet.thibaut.payment.domain.CustomerSupport;
import org.springframework.stereotype.Service;

@Service
public class AlertTransactionFailure implements CommandHandler<AlertTransactionFailureCommand, CommandResponse<Event>> {
    private final CustomerSupport customerSupport;

    public AlertTransactionFailure(CustomerSupport customerSupport) {
        this.customerSupport = customerSupport;
    }

    public CommandResponse<Event> execute(AlertTransactionFailureCommand command) {
        customerSupport.alertTransactionFailure(command.transactionId(), command.cartId(), command.amount());

        return new CommandResponse<>(List.of());
    }

    @Override
    public Class listenTo() {
        return AlertTransactionFailureCommand.class;
    }
}
