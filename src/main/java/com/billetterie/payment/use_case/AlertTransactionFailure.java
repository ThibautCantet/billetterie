package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.CustomerSupport;
import org.springframework.stereotype.Service;

@Service
public class AlertTransactionFailure {
    private final CustomerSupport customerSupport;

    public AlertTransactionFailure(CustomerSupport customerSupport) {
        this.customerSupport = customerSupport;
    }

    public void execute(AlertTransactionFailureCommand command) {
        customerSupport.alertTransactionFailure(command.transactionId(), command.cartId(), command.amount());
    }
}
