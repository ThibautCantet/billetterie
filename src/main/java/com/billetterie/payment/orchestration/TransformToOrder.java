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
        //TODO: transform to order, see order.transformToOrder(...)
        //TODO: check if the order.isCompleted() then send a confirmation email, see confirmationService.send(...)
            //TODO: return a succeeded result, see PayAndTransformToOrderResult.succeeded();

        //TODO: else the order is not completed then cancel transaction, see bank.cancel(...)
            //TODO: if transaction is not cancelled then alert customer support, see customerSupport.alertTransactionFailure(...)

        //TODO: anyway, return a failed result, see PayAndTransformToOrderResult.failed(...)
        // tip: use getErrorCartUrl(cartId, amount)) for last parameter

        return new PayAndTransformToOrderResult(PaymentStatus.FAILED, null, null, null, null, null);
    }

    public static String getErrorCartUrl(String cartId, float amount) {
        return "/cart?error=true&cartId=" + cartId + "&amount=" + amount;
    }
}
