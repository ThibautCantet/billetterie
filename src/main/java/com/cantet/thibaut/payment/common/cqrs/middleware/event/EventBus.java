package com.cantet.thibaut.payment.common.cqrs.middleware.event;


import java.util.Set;

import com.cantet.thibaut.payment.common.cqrs.command.Command;
import com.cantet.thibaut.payment.common.cqrs.event.Event;

public interface EventBus {
    <C extends Command> C publish(Event event);

    void resetPublishedEvents();

    Set<Event> getPublishedEvents();
}
