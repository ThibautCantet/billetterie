package com.billetterie.payment.common.cqrs.command;

public interface CommandHandler<C extends Command, R extends CommandResponse> {

    R execute(C command);

    Class listenTo();
}
