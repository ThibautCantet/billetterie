package com.billetterie.payment.use_case;

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
        //TODO: implement the logic to send confirmation email
        return null;
    }

    @Override
    public Class<SendConfirmationEmailCommand> listenTo() {
        return SendConfirmationEmailCommand.class;
    }
}
