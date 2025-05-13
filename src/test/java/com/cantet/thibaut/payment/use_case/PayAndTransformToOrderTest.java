package com.cantet.thibaut.payment.use_case;


import com.cantet.thibaut.payment.domain.Bank;
import com.cantet.thibaut.payment.domain.CustomerSupport;
import com.cantet.thibaut.payment.domain.Order;
import com.cantet.thibaut.payment.domain.Orders;
import com.cantet.thibaut.payment.domain.PayAndTransformToOrderResult;
import com.cantet.thibaut.payment.domain.Payment;
import com.cantet.thibaut.payment.domain.PaymentStatus;
import com.cantet.thibaut.payment.domain.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayAndTransformToOrderTest {

    private static final String CART_ID = "123456";
    private static final String ORDER_ID = "654654";
    private static final String CARD_NUMBER = "1234567890123456";
    private static final String EXPIRATION_DATE = "12/27";
    private static final String CYPHER = "123";
    private static final float AMOUNT = 100.0f;
    private static final String TRANSACTION_ID = "324234243234";

    @InjectMocks
    private PayAndTransformToOrder payAndTransformToOrder;
    @Mock
    private Bank bank;

    @Mock
    private Orders orders;
    @Mock
    private CustomerSupport customerSupport;

    @Test
    public void should_return_ok_when_payment_succeeds_and_transform_to_order_succeeds() {
        // given
        var succeededTransaction = new Transaction(TRANSACTION_ID, PaymentStatus.SUCCESS, null);

        when(bank.pay(new Payment(CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT)))
                .thenReturn(succeededTransaction);

        var order = new Order(ORDER_ID, AMOUNT);
        when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(order);

        // when
        var result = payAndTransformToOrder.execute(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT);

        // then
        assertThat(result).extracting(PayAndTransformToOrderResult::status,
                        PayAndTransformToOrderResult::transactionId,
                        PayAndTransformToOrderResult::orderId,
                        PayAndTransformToOrderResult::redirectUrl,
                        PayAndTransformToOrderResult::amount)
                .containsExactly(PaymentStatus.SUCCESS, "324234243234", ORDER_ID, "/confirmation/654654?amount=100.0", AMOUNT);
    }

    @Test
    public void should_return_ok_with_redirection_when_payment_requires_3DS() {
        // given
        var transactionToValidate = new Transaction(TRANSACTION_ID, PaymentStatus.PENDING, "/3ds");

        when(bank.pay(new Payment(CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT)))
                .thenReturn(transactionToValidate);

        // when
        var result = payAndTransformToOrder.execute(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT);

        // then
        assertThat(result).extracting(PayAndTransformToOrderResult::status,
                        PayAndTransformToOrderResult::transactionId,
                        PayAndTransformToOrderResult::orderId,
                        PayAndTransformToOrderResult::redirectUrl,
                        PayAndTransformToOrderResult::amount)
                .containsExactly(PaymentStatus.PENDING, TRANSACTION_ID, null, "/3ds", AMOUNT);
    }

    @Test
    public void should_return_failed_when_payment_fails() {
        // given
        var failedTransaction = new Transaction(TRANSACTION_ID, PaymentStatus.FAILED, null);

        when(bank.pay(new Payment(CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT)))
                .thenReturn(failedTransaction);

        // when
        var result = payAndTransformToOrder.execute(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT);

        // then
        assertThat(result).extracting(PayAndTransformToOrderResult::status,
                        PayAndTransformToOrderResult::transactionId,
                        PayAndTransformToOrderResult::orderId,
                        PayAndTransformToOrderResult::redirectUrl,
                        PayAndTransformToOrderResult::amount)
                .containsExactly(PaymentStatus.FAILED, TRANSACTION_ID, null, null, 0.0f);
    }

    @Test
    public void should_return_failed_and_cancel_transaction_when_payment_success_but_transform_to_order_fails() {
        // given
        var succeededTransaction = new Transaction(TRANSACTION_ID, PaymentStatus.SUCCESS, null);

        when(bank.pay(new Payment(CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT)))
                .thenReturn(succeededTransaction);

        var failedOrder = new Order(null, 0f);
        when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(failedOrder);

        when(bank.cancel(TRANSACTION_ID)).thenReturn(true);

        // when
        var result = payAndTransformToOrder.execute(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT);

        // then
        assertThat(result).extracting(PayAndTransformToOrderResult::status,
                        PayAndTransformToOrderResult::transactionId,
                        PayAndTransformToOrderResult::orderId,
                        PayAndTransformToOrderResult::redirectUrl,
                        PayAndTransformToOrderResult::amount)
                .containsExactly(PaymentStatus.FAILED, TRANSACTION_ID, null, "/panier", 0.0f);

        verify(bank).cancel(TRANSACTION_ID);

        verify(customerSupport, never()).alertTransactionFailure(any(), any(), any());
    }

    @Test
    public void should_return_failed_and_alert_when_payment_success_but_transform_to_order_fails_and_cancel_transaction_fails() {
        // given
        var succeededTransaction = new Transaction(TRANSACTION_ID, PaymentStatus.SUCCESS, null);

        when(bank.pay(new Payment(CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT)))
                .thenReturn(succeededTransaction);

        var failedOrder = new Order(null, 0f);
        when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(failedOrder);

        when(bank.cancel(TRANSACTION_ID)).thenReturn(false);

        // when
        var result = payAndTransformToOrder.execute(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT);

        // then
        assertThat(result).extracting(PayAndTransformToOrderResult::status,
                        PayAndTransformToOrderResult::transactionId,
                        PayAndTransformToOrderResult::orderId,
                        PayAndTransformToOrderResult::redirectUrl,
                        PayAndTransformToOrderResult::amount)
                .containsExactly(PaymentStatus.FAILED, TRANSACTION_ID, null, "/panier", 0.0f);

        verify(bank).cancel(TRANSACTION_ID);

        verify(customerSupport).alertTransactionFailure(TRANSACTION_ID, CART_ID, AMOUNT);
    }


}
