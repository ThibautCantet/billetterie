package com.billetterie.payment.common.cqrs.command;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.billetterie.payment.common.cqrs.event.Event;

public class CommandResponse<E extends Event> {

    List<E> events;

    public CommandResponse(E event) {
        this.events = new ArrayList<>();
        events.add(event);
    }

    public CommandResponse(List<E> event) {
        this.events = new ArrayList<>();
        events.addAll(event);
    }

    public Optional<? extends Event> findFirst(Class<? extends Event> clazz) {
        return events.stream()
                .filter(e -> e.getClass().equals(clazz))
                .findFirst();
    }

    public E first() {
        return events.getLast();
    }

    public <T extends E> T firstAs(Class<T> clazz) {
        return clazz.cast(findFirst(clazz).orElse(null));
    }

    public List<E> events() {
        return events;
    }
}
