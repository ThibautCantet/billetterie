package com.billetterie.payment.common.cqrs.event;

public interface EventHandler<T extends Event> {
    Class<T> listenTo();

}
