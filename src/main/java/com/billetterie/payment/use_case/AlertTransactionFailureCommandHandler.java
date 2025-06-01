package com.billetterie.payment.use_case;

import java.util.List;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.CustomerSupport;
import com.billetterie.payment.domain.CustomerSupportAlerted;
import org.springframework.stereotype.Service;

@Service
public class AlertTransactionFailureCommandHandler implements CommandHandler<AlertTransactionFailureCommand, CommandResponse<Event>> {
    private final CustomerSupport customerSupport;

    public AlertTransactionFailureCommandHandler(CustomerSupport customerSupport) {
        this.customerSupport = customerSupport;
    }

    public CommandResponse<Event> handle(AlertTransactionFailureCommand command) {
        customerSupport.alertTransactionFailure(command.transactionId(), command.cartId(), command.amount());
        return new CommandResponse<>(new CustomerSupportAlerted());
    }

    @Override
    public Class listenTo() {
        return AlertTransactionFailureCommand.class;
    }
}
