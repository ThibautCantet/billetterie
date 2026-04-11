package com.billetterie.payment.choregraphy.handler;

import com.billetterie.payment.common.cqrs.command.CommandHandler;
import com.billetterie.payment.common.cqrs.command.CommandResponse;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.domain.ConfirmationEmailSent;
import com.billetterie.payment.domain.ConfirmationService;
import org.springframework.stereotype.Service;

@Service
public class SendConfirmationEmailCommandHandler implements CommandHandler<SendConfirmationEmailCommand, CommandResponse<Event>> {

    private final ConfirmationService confirmationService;

    public SendConfirmationEmailCommandHandler(ConfirmationService confirmationService) {
        this.confirmationService = confirmationService;
    }

    @Override
    public CommandResponse<Event> handle(SendConfirmationEmailCommand command) {
        confirmationService.send(command.email(), command.orderId(), command.amount());
        return new CommandResponse<>(new ConfirmationEmailSent(command.email(), command.orderId(), command.amount()));
    }

    @Override
    public Class<SendConfirmationEmailCommand> listenTo() {
        return SendConfirmationEmailCommand.class;
    }
}
