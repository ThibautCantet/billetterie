package com.cantet.thibaut.payment.common.cqrs.middleware.queries;

import com.cantet.thibaut.payment.common.cqrs.query.Query;
import com.cantet.thibaut.payment.common.cqrs.query.QueryResponse;

public class QueryBusLogger implements QueryBus {

    private final QueryBus queryBus;

    public QueryBusLogger(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    @Override
    public <R extends QueryResponse, C extends Query> R dispatch(C query) {
        final R queryResponse = this.queryBus.dispatch(query);
        System.out.println(query.toString());
        return queryResponse;
    }
}
