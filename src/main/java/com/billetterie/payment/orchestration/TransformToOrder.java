package com.billetterie.payment.orchestration;

import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.CustomerSupport;
import com.billetterie.payment.domain.ConfirmationService;
import com.billetterie.payment.domain.Order;
import com.billetterie.payment.domain.Orders;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransformToOrder {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformToOrder.class);

    private final Orders orders;
    private final Bank bank;
    private final CustomerSupport customerSupport;
    private final ConfirmationService confirmationService;

    public TransformToOrder(Orders orders, Bank bank, CustomerSupport customerSupport, ConfirmationService confirmationService) {
        this.orders = orders;
        this.bank = bank;
        this.customerSupport = customerSupport;
        this.confirmationService = confirmationService;
    }

    public PayAndTransformToOrderResult execute(String transactionId, String cartId, float amount, String email) {
        //TODO: transform to order with orders.transformToOrder(cartId, amount) to get an order
        //TODO: check if the order.isCompleted() then send a confirmation email with
        // confirmationService.send(email, order.id(), order.amount());

        //TODO: return a succeeded result with
        // PayAndTransformToOrderResult.succeeded(
        //         transactionId,
        //         order.id(),
        //         amount);

        //TODO: else the order is not completed then
        //TODO: cancel transaction using bank.cancel() with transactionId and amount if order not completed
        //TODO: if transaction is not cancelled then alert customer support with
        // customerSupport.alertTransactionFailure(transactionId, cartId, amount);

        //TODO: anyway, return a failed result with
        // PayAndTransformToOrderResult.failed(
        //                    transactionId,
        //                    getErrorCartUrl(cartId, amount));

        return new PayAndTransformToOrderResult(PaymentStatus.FAILED, null, null, null, null, null);
    }

    public static String getErrorCartUrl(String cartId, float amount) {
        return "/cart?error=true&cartId=" + cartId + "&amount=" + amount;
    }
}
