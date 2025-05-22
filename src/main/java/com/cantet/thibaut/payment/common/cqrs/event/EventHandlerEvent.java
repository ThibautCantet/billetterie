package com.cantet.thibaut.payment.common.cqrs.event;

public abstract class EventHandlerEvent<E extends Event> implements EventHandlerReturnEvent<E> {

    @Override
    public EventHandlerType getType() {
        return EventHandlerType.EVENT;
    }
}
