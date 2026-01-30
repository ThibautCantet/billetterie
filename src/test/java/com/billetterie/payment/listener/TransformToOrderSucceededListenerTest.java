package com.billetterie.payment.listener;

import com.billetterie.payment.domain.TransformToOrderSucceeded;
import com.billetterie.payment.use_case.SendConfirmationEmailCommand;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TransformToOrderSucceededListenerTest {

    private static final String EMAIL = "client@mail.com";
    private static final String ORDER_ID = "order123";
    private static final Float AMOUNT = 100.0f;

    private TransformToOrderSucceededListener transformToOrderSucceededListener;

    @BeforeEach
    void setUp() {
        transformToOrderSucceededListener = new TransformToOrderSucceededListener();
    }

    @Test
    void should_return_SendConfirmationEmailCommand() {
        var transformToOrderSucceeded = new TransformToOrderSucceeded(EMAIL, ORDER_ID, AMOUNT);

        var command = transformToOrderSucceededListener.handle(transformToOrderSucceeded);

        assertThat(command)
                .isExactlyInstanceOf(SendConfirmationEmailCommand.class)
                .asInstanceOf(InstanceOfAssertFactories.type(SendConfirmationEmailCommand.class))
                .extracting(SendConfirmationEmailCommand::email,
                        SendConfirmationEmailCommand::orderId,
                        SendConfirmationEmailCommand::amount)
                .containsExactly(EMAIL, ORDER_ID, AMOUNT);
    }

    @Test
    void should_listen_to_TransformToOrderSucceeded() {
        var listenTo = transformToOrderSucceededListener.listenTo();

        assertThat(listenTo).isEqualTo(TransformToOrderSucceeded.class);
    }
}
