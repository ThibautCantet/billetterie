package com.billetterie.payment.common.cqrs.middleware.event;


import java.util.List;

import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.common.cqrs.event.EventHandler;


public class EventBusFactory {
    private final List<EventHandler<? extends Event>> eventHandlers;

    public EventBusFactory(List<EventHandler<? extends Event>> eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    public EventBus build() {
        EventBus eventBusDispatcher = new EventBusDispatcher(eventHandlers);

        return new EventBusLogger(eventBusDispatcher);
    }

}
