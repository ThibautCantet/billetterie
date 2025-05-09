package com.cantet.thibaut.payment.infrastructure.service;

import com.cantet.thibaut.payment.domain.CustomerSupport;
import org.springframework.stereotype.Service;

@Service
public class EmailCustomerSupport implements CustomerSupport {
    @Override
    public void alertTransactionFailure(String transactionId, String cartId, Float amount) {

    }
}
