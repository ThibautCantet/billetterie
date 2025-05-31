package com.billetterie.payment.common.cqrs.event;


public interface EventHandlerReturnEvent<E extends Event> extends EventHandler<E> {

    <Ev extends Event> Ev handle(E event);

}
