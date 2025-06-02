package com.billetterie.payment.common.cqrs.event;

public abstract class EventHandlerCommand<E extends Event> implements EventHandlerReturnCommand<E> {
}
