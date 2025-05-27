package com.billetterie.payment.common.cqrs.query;


import com.billetterie.payment.common.cqrs.event.Event;

public record QueryResponse<T>(T value, Event event) {
    public QueryResponse(Event event) {
        this(null, event);
    }
}
