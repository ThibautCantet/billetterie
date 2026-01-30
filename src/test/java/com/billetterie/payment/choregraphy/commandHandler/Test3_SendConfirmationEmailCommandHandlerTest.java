package com.billetterie.payment.choregraphy.commandHandler;

import com.billetterie.payment.domain.ConfirmationEmailSent;
import com.billetterie.payment.domain.ConfirmationService;
import com.billetterie.payment.choregraphy.handler.SendConfirmationEmailCommand;
import com.billetterie.payment.choregraphy.handler.SendConfirmationEmailCommandHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class Test3_SendConfirmationEmailCommandHandlerTest {

    private static final String EMAIL = "client@mail.com";
    private static final String ORDER_ID = "order123";
    private static final Float AMOUNT = 100.0f;

    private SendConfirmationEmailCommandHandler sendConfirmationEmailCommandHandler;

    @Mock
    private ConfirmationService confirmationService;

    @BeforeEach
    void setUp() {
        sendConfirmationEmailCommandHandler = new SendConfirmationEmailCommandHandler(confirmationService);
    }

    @Nested
    class Handle {
        @Test
        void should_return_ConfirmationEmailSent_when_email_sent_successfully() {
            var command = new SendConfirmationEmailCommand(EMAIL, ORDER_ID, AMOUNT);

            var result = sendConfirmationEmailCommandHandler.handle(command);

            assertThat(result.firstAs(ConfirmationEmailSent.class))
                    .extracting(ConfirmationEmailSent::email,
                            ConfirmationEmailSent::orderId,
                            ConfirmationEmailSent::amount)
                    .containsExactly(EMAIL, ORDER_ID, AMOUNT);

            verify(confirmationService).send(EMAIL, ORDER_ID, AMOUNT);
        }
    }

    @Test
    void should_return_SendConfirmationEmailCommand() {
        var listenTo = sendConfirmationEmailCommandHandler.listenTo();

        assertThat(listenTo).isEqualTo(SendConfirmationEmailCommand.class);
    }
}
