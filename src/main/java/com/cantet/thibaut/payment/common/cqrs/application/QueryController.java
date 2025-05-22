package com.cantet.thibaut.payment.common.cqrs.application;

import com.cantet.thibaut.payment.common.cqrs.middleware.queries.QueryBus;
import com.cantet.thibaut.payment.common.cqrs.middleware.queries.QueryBusFactory;

public abstract class QueryController {
    private QueryBus queryBus;
    private final QueryBusFactory queryBusFactory;

    public QueryController(QueryBusFactory queryBusFactory) {
        this.queryBusFactory = queryBusFactory;
    }

    protected QueryBus getQueryBus() {
        if (queryBus == null) {
            this.queryBus = queryBusFactory.build();
        }
        return queryBus;
    }

}
