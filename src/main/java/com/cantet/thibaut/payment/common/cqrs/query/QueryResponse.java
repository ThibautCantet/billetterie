package com.cantet.thibaut.payment.common.cqrs.query;


import com.cantet.thibaut.payment.common.cqrs.event.Event;

public record QueryResponse<T>(T value, Event event) {
    public QueryResponse(Event event) {
        this(null, event);
    }
}
