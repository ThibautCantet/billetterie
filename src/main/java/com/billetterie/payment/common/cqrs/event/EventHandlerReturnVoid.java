package com.billetterie.payment.common.cqrs.event;


public interface EventHandlerReturnVoid<E extends Event> extends EventHandler<E> {

    void handle(E event);

}
