package com.cantet.thibaut.payment.common.cqrs.event;


public interface EventHandlerReturnEvent<E extends Event> extends EventHandler<E> {

    <Ev extends Event> Ev execute(E event);

}
