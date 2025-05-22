package com.cantet.thibaut.payment.common.cqrs.query;

public interface QueryHandler<Q extends Query, R extends QueryResponse> {

    R execute(Q query);

    Class listenTo();
}
