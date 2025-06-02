package com.billetterie.payment.common.cqrs.middleware.event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.billetterie.payment.common.cqrs.command.Command;
import com.billetterie.payment.common.cqrs.event.Event;
import com.billetterie.payment.common.cqrs.event.EventHandler;
import com.billetterie.payment.common.cqrs.event.EventHandlerCommand;


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
            if (handler instanceof EventHandlerCommand eventHandlerCommand) {
                Command command = eventHandlerCommand.handle(event);
                if (command != null) {
                    commands.add(command);
                }
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
