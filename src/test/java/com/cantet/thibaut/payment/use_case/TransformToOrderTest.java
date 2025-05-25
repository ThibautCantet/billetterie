package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.domain.Bank;
import com.cantet.thibaut.payment.domain.CustomerSupport;
import com.cantet.thibaut.payment.domain.Order;
import com.cantet.thibaut.payment.domain.OrderCreated;
import com.cantet.thibaut.payment.domain.OrderNotCreated;
import com.cantet.thibaut.payment.domain.Orders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.cantet.thibaut.payment.domain.PaymentStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransformToOrderTest {

    private static final String CART_ID = "123456";
    private static final String ORDER_ID = "654654";
    private static final float AMOUNT = 100.0f;
    private static final String TRANSACTION_ID = "324234243234";

    private TransformToOrder transformToOrder;

    @Mock
    private Orders orders;

    @Mock
    private Bank bank;
    @Mock
    private CustomerSupport customerSupport;

    @BeforeEach
    void setUp() {
        transformToOrder = new TransformToOrder(orders, new CancelTransaction(bank), new AlertTransactionFailure(customerSupport));
    }

    @Nested
    class Execute {
        @Test
        void should_return_ok_when_transform_to_order_succeeds() {
            // given
            var order = new Order(ORDER_ID, AMOUNT);
            when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(order);

            // when
            var result = transformToOrder.execute(new TransformToOrderCommand(TRANSACTION_ID, CART_ID, AMOUNT));

            // then
            assertThat(result.firstAs(OrderCreated.class)).extracting(OrderCreated::status,
                            OrderCreated::transactionId,
                            OrderCreated::orderId,
                            OrderCreated::redirectUrl,
                            OrderCreated::amount)
                    .containsExactly(SUCCESS, TRANSACTION_ID, ORDER_ID, "/confirmation/654654?amount=100.0", AMOUNT);
        }

        @Test
        void should_return_failed_and_cancel_transaction_when_transform_to_order_fails() {
            // given
            var order = new Order(null, 0f);
            when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(order);

            when(bank.cancel(TRANSACTION_ID, AMOUNT)).thenReturn(true);

            // when
            var result = transformToOrder.execute(new TransformToOrderCommand(TRANSACTION_ID, CART_ID, AMOUNT));

            // then
            assertThat(result.firstAs(OrderNotCreated.class)).extracting(OrderNotCreated::amount,
                            OrderNotCreated::transactionId,
                            OrderNotCreated::redirectUrl)
                    .containsExactly(AMOUNT,
                            TRANSACTION_ID,
                            "/cart?error=true&cartId=123456&amount=100.0");

            verify(bank).cancel(TRANSACTION_ID, AMOUNT);

            verify(customerSupport, never()).alertTransactionFailure(any(), any(), any());
        }

        @Test
        void should_return_failed_and_alert_when_transform_to_order_fails_and_cancel_transaction_fails() {
            // given
            var order = new Order(null, 0f);
            when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(order);

            when(bank.cancel(TRANSACTION_ID, AMOUNT)).thenReturn(false);

            // when
            var result = transformToOrder.execute(new TransformToOrderCommand(TRANSACTION_ID, CART_ID, AMOUNT));

            // then
            assertThat(result.firstAs(OrderNotCreated.class)).extracting(OrderNotCreated::amount,
                            OrderNotCreated::transactionId,
                            OrderNotCreated::redirectUrl)
                    .containsExactly(AMOUNT,
                            TRANSACTION_ID,
                            "/cart?error=true&cartId=123456&amount=100.0");

            verify(bank).cancel(TRANSACTION_ID, AMOUNT);

            verify(customerSupport).alertTransactionFailure(TRANSACTION_ID, CART_ID, AMOUNT);
        }
    }

    @Test
    void listenTo_should_return_TransformToOrderCommand() {
        assertThat(transformToOrder.listenTo()).isEqualTo(TransformToOrderCommand.class);
    }
}
