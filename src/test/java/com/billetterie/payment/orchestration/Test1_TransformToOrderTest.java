package com.billetterie.payment.orchestration;

import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.ConfirmationService;
import com.billetterie.payment.domain.CustomerSupport;
import com.billetterie.payment.domain.Order;
import com.billetterie.payment.domain.Orders;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Test1_TransformToOrderTest {

    private static final String CART_ID = "123456";
    private static final String ORDER_ID = "654654";
    private static final float AMOUNT = 100.0f;
    private static final String EMAIL = "client@mail.com";
    private static final String TRANSACTION_ID = "324234243234";
    private static final String REDIRECT_URL = "/confirmation/654654?amount=100.0";
    private static final String REDIRECT_URL_ERROR = "/cart?error=true&cartId=123456&amount=100.0";
    private static final Order NEW_ORDER = new Order(ORDER_ID, AMOUNT);
    private static final Order FAILED_ORDER = new Order(null, 0f);

    private TransformToOrder transformToOrder;

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
        transformToOrder = new TransformToOrder(orders, bank, customerSupport, confirmationService);
    }

    @Test
    void should_return_success_and_send_confirmation_email_when_transform_to_order_succeeds() {
        // given
        when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(NEW_ORDER);

        // when
        PayAndTransformToOrderResult result = transformToOrder.execute(TRANSACTION_ID, CART_ID, AMOUNT, EMAIL);

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

        verify(orders).transformToOrder(CART_ID, AMOUNT);
        verify(confirmationService).send(EMAIL, ORDER_ID, AMOUNT);
    }

    @Test
    void should_return_failed_and_cancel_transaction_when_transform_to_order_fails() {
        // given
        when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(FAILED_ORDER);
        when(bank.cancel(TRANSACTION_ID, AMOUNT)).thenReturn(true);

        // when
        PayAndTransformToOrderResult result = transformToOrder.execute(TRANSACTION_ID, CART_ID, AMOUNT, EMAIL);

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
                REDIRECT_URL_ERROR,
                null
        );

        verify(orders).transformToOrder(CART_ID, AMOUNT);
        verify(bank).cancel(TRANSACTION_ID, AMOUNT);
        verify(customerSupport, never()).alertTransactionFailure(any(), any(), anyFloat());
        verify(confirmationService, never()).send(any(), any(), anyFloat());
    }

    @Test
    void should_return_failed_and_alert_customer_support_when_transform_to_order_fails_and_cancel_transaction_fails() {
        // given
        when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(FAILED_ORDER);
        when(bank.cancel(TRANSACTION_ID, AMOUNT)).thenReturn(false);

        // when
        PayAndTransformToOrderResult result = transformToOrder.execute(TRANSACTION_ID, CART_ID, AMOUNT, EMAIL);

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
                REDIRECT_URL_ERROR,
                null
        );

        verify(orders).transformToOrder(CART_ID, AMOUNT);
        verify(bank).cancel(TRANSACTION_ID, AMOUNT);
        verify(customerSupport).alertTransactionFailure(TRANSACTION_ID, CART_ID, AMOUNT);
        verify(confirmationService, never()).send(any(), any(), anyFloat());
    }
}

