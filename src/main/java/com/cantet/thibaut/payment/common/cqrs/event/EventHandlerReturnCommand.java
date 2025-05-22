package com.cantet.thibaut.payment.common.cqrs.event;

import com.cantet.thibaut.payment.common.cqrs.command.Command;

public interface EventHandlerReturnCommand<E extends Event> extends EventHandler<E> {

    <C extends Command> C execute(E event);

}
