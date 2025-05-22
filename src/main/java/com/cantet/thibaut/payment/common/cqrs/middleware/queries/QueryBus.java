package com.cantet.thibaut.payment.common.cqrs.middleware.queries;

import com.cantet.thibaut.payment.common.cqrs.query.Query;
import com.cantet.thibaut.payment.common.cqrs.query.QueryResponse;

public interface QueryBus {
    <R extends QueryResponse, C extends Query> R dispatch(C query);
}
