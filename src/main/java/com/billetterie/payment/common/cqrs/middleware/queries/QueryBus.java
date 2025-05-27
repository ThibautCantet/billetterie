package com.billetterie.payment.common.cqrs.middleware.queries;

import com.billetterie.payment.common.cqrs.query.Query;
import com.billetterie.payment.common.cqrs.query.QueryResponse;

public interface QueryBus {
    <R extends QueryResponse, C extends Query> R dispatch(C query);
}
