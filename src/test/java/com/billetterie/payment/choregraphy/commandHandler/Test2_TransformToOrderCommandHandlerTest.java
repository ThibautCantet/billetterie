package com.billetterie.payment.choregraphy.commandHandler;

import com.billetterie.payment.domain.Order;
import com.billetterie.payment.domain.OrderCreated;
import com.billetterie.payment.domain.OrderNotCreated;
import com.billetterie.payment.domain.Orders;
import com.billetterie.payment.domain.TransformToOrderSucceeded;
import com.billetterie.payment.choregraphy.handler.TransformToOrderCommand;
import com.billetterie.payment.choregraphy.handler.TransformToOrderCommandHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class Test2_TransformToOrderCommandHandlerTest {

    private static final String CART_ID = "123456";
    private static final String ORDER_ID = "654654";
    private static final float AMOUNT = 100.0f;
    private static final String TRANSACTION_ID = "324234243234";
    private static final String EMAIL = "client@mail.com";

    private TransformToOrderCommandHandler transformToOrderCommandHandler;

    @Mock
    private Orders orders;

    @BeforeEach
    void setUp() {
        transformToOrderCommandHandler = new TransformToOrderCommandHandler(orders);
    }

    @Nested
    class Handle {

        @Test
        void should_return_OrderCreated_and_TransformToOrderSucceeded_when_transform_to_order_succeeds() {
            // given
            var order = new Order(ORDER_ID, AMOUNT);
            when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(order);

            // when
            var result = transformToOrderCommandHandler.handle(new TransformToOrderCommand(TRANSACTION_ID, CART_ID, AMOUNT, EMAIL));

            // then
            assertThat(result.events()).containsExactly(
                    OrderCreated.of(TRANSACTION_ID, ORDER_ID, AMOUNT, EMAIL),
                    new TransformToOrderSucceeded(EMAIL, ORDER_ID, AMOUNT)
            );;
        }

        @Test
        void should_return_OrderNotCreated_when_transform_to_order_fails() {
            // given
            var order = new Order(null, 0f);
            when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(order);

            // when
            var result = transformToOrderCommandHandler.handle(new TransformToOrderCommand(TRANSACTION_ID, CART_ID, AMOUNT, EMAIL));

            // then
            assertThat(result.firstAs(OrderNotCreated.class)).extracting(OrderNotCreated::amount,
                            OrderNotCreated::transactionId,
                            OrderNotCreated::redirectUrl)
                    .containsExactly(AMOUNT,
                            TRANSACTION_ID,
                            "/cart?error=true&cartId=123456&amount=100.0");
        }
    }

    @Test
    void listenTo_should_return_TransformToOrderCommand() {
        assertThat(transformToOrderCommandHandler.listenTo()).isEqualTo(TransformToOrderCommand.class);
    }
}
