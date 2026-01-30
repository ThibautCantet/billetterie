package com.billetterie.payment.orchestration;

import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.ConfirmationService;
import com.billetterie.payment.domain.CustomerSupport;
import com.billetterie.payment.domain.Order;
import com.billetterie.payment.domain.Orders;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.Payment;
import com.billetterie.payment.domain.PaymentStatus;
import com.billetterie.payment.domain.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Test2_PayAndTransformToOrderTest {

    private static final String CART_ID = "123456";
    private static final String ORDER_ID = "654654";
    private static final String CARD_NUMBER = "1234567890123456";
    private static final String EXPIRATION_DATE = "12/27";
    private static final String CYPHER = "123";
    private static final float AMOUNT = 100.0f;
    private static final String EMAIL = "client@mail.com";
    private static final Payment PAYMENT = new Payment(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT, EMAIL);
    private static final String TRANSACTION_ID = "324234243234";
    private static final String REDIRECT_URL_3DS = "/3ds";
    private static final String REDIRECT_URL = "/confirmation/654654?amount=100.0";
    private static final String ERROR_REDIRECT_URL = "/cart?error=true&cartId=123456&amount=100.0";
    private static final Transaction PENDING_TRANSACTION = new Transaction(TRANSACTION_ID, PaymentStatus.PENDING, REDIRECT_URL_3DS);
    private static final Transaction FAILED_TRANSACTION = new Transaction(TRANSACTION_ID, PaymentStatus.FAILED, null);
    private static final Transaction SUCCEEDED_TRANSACTION = new Transaction(TRANSACTION_ID, PaymentStatus.SUCCESS, null);
    private static final Order NEW_ORDER = new Order(ORDER_ID, AMOUNT);
    private static final Order FAILED_ORDER = new Order(null, 0f);

    private PayAndTransformToOrder payAndTransformToOrder;

    @Mock
    private Bank bank;

    @Mock
    private Orders orders;

    @Mock
    private CustomerSupport customerSupport;

    @Mock
    private ConfirmationService confirmationService;

    @BeforeEach
    void setUp() {
        var transformToOrder = new TransformToOrder(orders, bank, customerSupport, confirmationService);
        payAndTransformToOrder = new PayAndTransformToOrder(bank, transformToOrder);
    }

    @Test
    void should_return_pending_with_redirect_url_when_3DS_required() {
        // given
        when(bank.pay(PAYMENT)).thenReturn(PENDING_TRANSACTION);

        // when
        PayAndTransformToOrderResult result = payAndTransformToOrder.execute(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT, EMAIL);

        // then
        assertThat(result).extracting(
                PayAndTransformToOrderResult::status,
                PayAndTransformToOrderResult::orderId,
                PayAndTransformToOrderResult::amount,
                PayAndTransformToOrderResult::transactionId,
                PayAndTransformToOrderResult::redirectUrl,
                PayAndTransformToOrderResult::email
        ).containsExactly(
                PaymentStatus.PENDING,
                null,
                AMOUNT,
                TRANSACTION_ID,
                REDIRECT_URL_3DS,
                EMAIL
        );

        verify(bank).pay(PAYMENT);
    }

    @Test
    void should_return_failed_result_with_only_status_when_payment_fails() {
        // given
        when(bank.pay(PAYMENT)).thenReturn(FAILED_TRANSACTION);

        // when
        PayAndTransformToOrderResult result = payAndTransformToOrder.execute(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT, EMAIL);

        // then
        assertThat(result).extracting(
                PayAndTransformToOrderResult::status,
                PayAndTransformToOrderResult::orderId,
                PayAndTransformToOrderResult::amount,
                PayAndTransformToOrderResult::transactionId,
                PayAndTransformToOrderResult::redirectUrl,
                PayAndTransformToOrderResult::email
        ).containsExactly(
                PaymentStatus.FAILED,
                null,
                0f,
                TRANSACTION_ID,
                null,
                null
        );

        verify(bank).pay(PAYMENT);
    }

    @Test
    void should_return_success_result_when_payment_and_order_succeed() {
        // given
        when(bank.pay(PAYMENT)).thenReturn(SUCCEEDED_TRANSACTION);
        when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(NEW_ORDER);

        // when
        PayAndTransformToOrderResult result = payAndTransformToOrder.execute(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT, EMAIL);

        // then
        assertThat(result).extracting(
                PayAndTransformToOrderResult::status,
                PayAndTransformToOrderResult::orderId,
                PayAndTransformToOrderResult::amount,
                PayAndTransformToOrderResult::transactionId,
                PayAndTransformToOrderResult::redirectUrl,
                PayAndTransformToOrderResult::email
        ).containsExactly(
                PaymentStatus.SUCCESS,
                ORDER_ID,
                AMOUNT,
                TRANSACTION_ID,
                REDIRECT_URL,
                null
        );

        verify(bank).pay(PAYMENT);
        verify(orders).transformToOrder(CART_ID, AMOUNT);
        verify(confirmationService).send(EMAIL, ORDER_ID, AMOUNT);
    }

    @Test
    void should_return_failed_and_cancel_transaction_when_payment_succeeds_but_transform_to_order_fails() {
        // given
        when(bank.pay(PAYMENT)).thenReturn(SUCCEEDED_TRANSACTION);
        when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(FAILED_ORDER);
        when(bank.cancel(TRANSACTION_ID, AMOUNT)).thenReturn(true);

        // when
        PayAndTransformToOrderResult result = payAndTransformToOrder.execute(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT, EMAIL);

        // then
        assertThat(result).extracting(
                PayAndTransformToOrderResult::status,
                PayAndTransformToOrderResult::orderId,
                PayAndTransformToOrderResult::amount,
                PayAndTransformToOrderResult::transactionId,
                PayAndTransformToOrderResult::redirectUrl,
                PayAndTransformToOrderResult::email
        ).containsExactly(
                PaymentStatus.FAILED,
                null,
                0f,
                TRANSACTION_ID,
                ERROR_REDIRECT_URL,
                null
        );

        verify(bank).pay(PAYMENT);
        verify(orders).transformToOrder(CART_ID, AMOUNT);
        verify(bank).cancel(TRANSACTION_ID, AMOUNT);
        verify(customerSupport, never()).alertTransactionFailure(any(), any(), anyFloat());
        verify(confirmationService, never()).send(any(), any(), anyFloat());
    }

    @Test
    void should_return_failed_and_alert_customer_support_when_payment_succeeds_but_transform_to_order_fails_and_cancel_fails() {
        // given
        when(bank.pay(PAYMENT)).thenReturn(SUCCEEDED_TRANSACTION);
        when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(FAILED_ORDER);
        when(bank.cancel(TRANSACTION_ID, AMOUNT)).thenReturn(false);

        // when
        PayAndTransformToOrderResult result = payAndTransformToOrder.execute(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT, EMAIL);

        // then
        assertThat(result).extracting(
                PayAndTransformToOrderResult::status,
                PayAndTransformToOrderResult::orderId,
                PayAndTransformToOrderResult::amount,
                PayAndTransformToOrderResult::transactionId,
                PayAndTransformToOrderResult::redirectUrl,
                PayAndTransformToOrderResult::email
        ).containsExactly(
                PaymentStatus.FAILED,
                null,
                0f,
                TRANSACTION_ID,
                ERROR_REDIRECT_URL,
                null
        );

        verify(bank).pay(PAYMENT);
        verify(orders).transformToOrder(CART_ID, AMOUNT);
        verify(bank).cancel(TRANSACTION_ID, AMOUNT);
        verify(customerSupport).alertTransactionFailure(TRANSACTION_ID, CART_ID, AMOUNT);
        verify(confirmationService, never()).send(any(), any(), anyFloat());
    }
}
