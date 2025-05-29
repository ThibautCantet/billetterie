package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.CustomerSupport;
import com.billetterie.payment.domain.Order;
import com.billetterie.payment.domain.OrderCreated;
import com.billetterie.payment.domain.OrderNotCreated;
import com.billetterie.payment.domain.Orders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.billetterie.payment.domain.PaymentStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransformToOrderCommandHandlerTest {

    private static final String CART_ID = "123456";
    private static final String ORDER_ID = "654654";
    private static final float AMOUNT = 100.0f;
    private static final String TRANSACTION_ID = "324234243234";

    private TransformToOrderCommandHandler transformToOrderCommandHandler;

    @Mock
    private Orders orders;

    @Mock
    private Bank bank;
    @Mock
    private CustomerSupport customerSupport;
    @Spy
    @InjectMocks
    private CancelTransactionCommandHandler cancelTransactionCommandHandler;
    @Spy
    @InjectMocks
    private AlertTransactionFailureCommandHandler alertTransactionFailureCommandHandler;

    @BeforeEach
    void setUp() {
        transformToOrderCommandHandler = new TransformToOrderCommandHandler(orders, bank, customerSupport, cancelTransactionCommandHandler, alertTransactionFailureCommandHandler);
    }

    @Nested
    class Handle {
        @Test
        void should_return_OrderCreated_when_transform_to_order_succeeds() {
            // given
            var order = new Order(ORDER_ID, AMOUNT);
            when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(order);

            // when
            var result = transformToOrderCommandHandler.handle(new TransformToOrderCommand(TRANSACTION_ID, CART_ID, AMOUNT));

            // then
            assertThat(result.firstAs(OrderCreated.class)).extracting(OrderCreated::status,
                            OrderCreated::transactionId,
                            OrderCreated::orderId,
                            OrderCreated::redirectUrl,
                            OrderCreated::amount)
                    .containsExactly(SUCCESS, TRANSACTION_ID, ORDER_ID, "/confirmation/654654?amount=100.0", AMOUNT);
        }

        @Test
        void should_return_OrderNotCreated_when_transform_to_order_fails() {
            // given
            var order = new Order(null, 0f);
            when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(order);

            // when
            var result = transformToOrderCommandHandler.handle(new TransformToOrderCommand(TRANSACTION_ID, CART_ID, AMOUNT));

            // then
            assertThat(result.firstAs(OrderNotCreated.class)).extracting(OrderNotCreated::amount,
                            OrderNotCreated::transactionId,
                            OrderNotCreated::redirectUrl)
                    .containsExactly(AMOUNT,
                            TRANSACTION_ID,
                            "/cart?error=true&cartId=123456&amount=100.0");

            verify(bank, never()).cancel(any(), any());

            verify(alertTransactionFailureCommandHandler, never()).handle(any());

            verify(customerSupport, never()).alertTransactionFailure(any(), any(), any());
        }
    }

    @Test
    void listenTo_should_return_TransformToOrderCommand() {
        assertThat(transformToOrderCommandHandler.listenTo()).isEqualTo(TransformToOrderCommand.class);
    }
}
