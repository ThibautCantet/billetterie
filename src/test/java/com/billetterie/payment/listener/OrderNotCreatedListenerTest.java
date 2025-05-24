package com.billetterie.payment.listener;

import com.billetterie.payment.domain.OrderNotCreated;
import com.billetterie.payment.use_case.CancelTransactionCommand;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OrderNotCreatedListenerTest {

    private static final String TRANSACTION_ID = "tx123";
    private static final float AMOUNT = 100.0f;
    private static final String CART_ON_ERROR_URL = "/cart?error=true&cartId=cartId&amount=100.0";
    private static final String CART_ID = "cartId";

    private OrderNotCreatedListener orderNotCreatedListener;

    @BeforeEach
    void setUp() {
        orderNotCreatedListener = new OrderNotCreatedListener();
    }

    @Test
    void should_return_new_CancelTransactionCommand() {
        var orderNotCreated = new OrderNotCreated(TRANSACTION_ID, AMOUNT, CART_ON_ERROR_URL, CART_ID);

        var command = orderNotCreatedListener.execute(orderNotCreated);

        assertThat(command)
                .isExactlyInstanceOf(CancelTransactionCommand.class)
                .asInstanceOf(InstanceOfAssertFactories.type(CancelTransactionCommand.class))
                .extracting(CancelTransactionCommand::transactionId,
                        CancelTransactionCommand::amount,
                        CancelTransactionCommand::cartId)
                .containsExactly(TRANSACTION_ID, AMOUNT, CART_ID);
    }

    @Test
    void should_return_OrderNotCreated() {
        var listenTo = orderNotCreatedListener.listenTo();

        assertThat(listenTo).isEqualTo(OrderNotCreated.class);
    }
}
