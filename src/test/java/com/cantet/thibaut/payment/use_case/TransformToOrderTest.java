package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.domain.Bank;
import com.cantet.thibaut.payment.domain.CustomerSupport;
import com.cantet.thibaut.payment.domain.Order;
import com.cantet.thibaut.payment.domain.Orders;
import com.cantet.thibaut.payment.domain.TransformToOrderResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.cantet.thibaut.payment.domain.TransformToOrderStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransformToOrderTest {

    private static final String CART_ID = "123456";
    private static final String ORDER_ID = "654654";
    private static final float AMOUNT = 100.0f;
    private static final String TRANSACTION_ID = "324234243234";

    @InjectMocks
    private TransformToOrder transformToOrder;

    @Mock
    private Orders orders;

    @Mock
    private Bank bank;
    @Mock
    private CustomerSupport customerSupport;

    @Test
    void should_return_ok_when_transform_to_order_succeeds() {
        // given
        var order = new Order(ORDER_ID, AMOUNT);
        when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(order);

        // when
        TransformToOrderResult result = transformToOrder.execute(TRANSACTION_ID, CART_ID, AMOUNT);

        // then
        assertThat(result).extracting(TransformToOrderResult::status,
                TransformToOrderResult::transactionId,
                TransformToOrderResult::orderId,
                TransformToOrderResult::redirectUrl,
                TransformToOrderResult::amount)
                .containsExactly(SUCCEEDED, TRANSACTION_ID, ORDER_ID, "confirmation", AMOUNT);
    }

    @Test
    void should_return_failed_and_cancel_transaction_when_transform_to_order_fails() {
        // given
        var order = new Order(null, 0f);
        when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(order);

        when(bank.cancel(TRANSACTION_ID)).thenReturn(true);

        // when
        TransformToOrderResult result = transformToOrder.execute(TRANSACTION_ID, CART_ID, AMOUNT);

        // then
        assertThat(result).extracting(TransformToOrderResult::status,
                TransformToOrderResult::transactionId,
                TransformToOrderResult::orderId,
                TransformToOrderResult::redirectUrl,
                TransformToOrderResult::amount)
                .containsExactly(FAILED, TRANSACTION_ID, null, "panier", null);

        verify(bank).cancel(TRANSACTION_ID);

        verify(customerSupport, never()).alertTransactionFailure(any(), any(), any());
    }

    @Test
    void should_return_failed_and_alert_when_transform_to_order_fails_and_cancel_transaction_fails() {
        // given
        var order = new Order(null, 0f);
        when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(order);

        when(bank.cancel(TRANSACTION_ID)).thenReturn(false);

        // when
        TransformToOrderResult result = transformToOrder.execute(TRANSACTION_ID, CART_ID, AMOUNT);

        // then
        assertThat(result).extracting(TransformToOrderResult::status,
                TransformToOrderResult::transactionId,
                TransformToOrderResult::orderId,
                TransformToOrderResult::redirectUrl,
                TransformToOrderResult::amount)
                .containsExactly(FAILED, TRANSACTION_ID, null, "panier", null);

        verify(bank).cancel(TRANSACTION_ID);

        verify(customerSupport).alertTransactionFailure(TRANSACTION_ID, CART_ID, AMOUNT);
    }

}
