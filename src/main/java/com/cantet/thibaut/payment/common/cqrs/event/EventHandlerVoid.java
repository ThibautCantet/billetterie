package com.cantet.thibaut.payment.common.cqrs.event;

public abstract class EventHandlerVoid<E extends Event> implements EventHandlerReturnVoid<E> {

    @Override
    public EventHandlerType getType() {
        return EventHandlerType.VOID;
    }
}
