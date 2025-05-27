package com.billetterie.payment.common.cqrs.event;

import com.billetterie.payment.common.cqrs.command.Command;

public interface EventHandlerReturnCommand<E extends Event> extends EventHandler<E> {

    <C extends Command> C execute(E event);

}
