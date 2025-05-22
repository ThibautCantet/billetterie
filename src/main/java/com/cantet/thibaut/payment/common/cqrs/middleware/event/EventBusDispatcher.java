package com.cantet.thibaut.payment.common.cqrs.middleware.event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.cantet.thibaut.payment.common.cqrs.command.Command;
import com.cantet.thibaut.payment.common.cqrs.event.Event;
import com.cantet.thibaut.payment.common.cqrs.event.EventHandler;
import com.cantet.thibaut.payment.common.cqrs.event.EventHandlerCommand;
import com.cantet.thibaut.payment.common.cqrs.event.EventHandlerEvent;
import com.cantet.thibaut.payment.common.cqrs.event.EventHandlerVoid;


public class EventBusDispatcher implements EventBus {

    private final Map<Class, ? extends List<? extends EventHandler>> eventHandlers;
    private final Set<Event> publishedEvents;

    public EventBusDispatcher(List<? extends EventHandler> eventHandlers) {
        this.eventHandlers = eventHandlers.stream()
                .collect(Collectors.groupingBy(EventHandler::listenTo));

        this.publishedEvents = new HashSet<>();
    }

    @Override
    public <C extends Command> C publish(Event event) {
        List<? extends EventHandler> eventHandlers = getListeners(event);

        var commands = new ArrayList<Command>();
        for (var handler : eventHandlers) {
            switch (handler.getType()) {
                case COMMAND:
                    Command command = ((EventHandlerCommand) handler).execute(event);
                    if (command != null) {
                        commands.add(command);
                    }
                    break;
                case EVENT:
                    Event newEvent = ((EventHandlerEvent) handler).execute(event);
                    if (newEvent != null) {
                        this.publish(newEvent);
                        publishedEvents.add(newEvent);
                    }
                    break;
                case VOID:
                    ((EventHandlerVoid) handler).execute(event);
                    break;
            }
        }

        return (C) commands.stream().findFirst().orElse(null);
    }

    @Override
    public void resetPublishedEvents() {
        publishedEvents.clear();
    }

    private List<? extends EventHandler> getListeners(Event event) {
        return this.eventHandlers.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(event))
                .flatMap(classEntry -> classEntry.getValue().stream())
                .collect(Collectors.toList());
    }

    @Override
    public Set<Event> getPublishedEvents() {
        return publishedEvents;
    }
}
