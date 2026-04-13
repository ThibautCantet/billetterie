package com.billetterie.payment.orchestration;

import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PayAndTransformToOrder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayAndTransformToOrder.class);

    private final Bank bank;
    private final TransformToOrder transformToOrder;

    public PayAndTransformToOrder(Bank bank, TransformToOrder transformToOrder) {
        this.bank = bank;
        this.transformToOrder = transformToOrder;
    }

    public PayAndTransformToOrderResult execute(String cartId, String cardNumber, String expirationDate, String cypher, float amount, String email) {
        //TODO: pay the order, see bank.pay(...)

        //TODO: check if transaction isPending() then return pending result, see PayAndTransformToOrderResult.pending(...)

        //TODO: check if transaction NOT hasSucceeded() then return failed, see PayAndTransformToOrderResult.failed(...)

        // we had 2 choices for the implementation:
        // a) call next use case transformToOrder.execute(transaction.id(), cartId, amount, email);
        // b) implement the rest of the orchestration
        // in this workshop, you can only call the other use case
        //TODO: call transform to order with orders.transformToOrder(cartId, amount) to get an order
        return new PayAndTransformToOrderResult(PaymentStatus.FAILED, null, null, null, null, null);
    }
}
