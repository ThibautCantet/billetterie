package com.cantet.thibaut.payment.common.cqrs.event;

public interface EventHandler<T extends Event> {
    Class<T> listenTo();

    EventHandlerType getType();
}
