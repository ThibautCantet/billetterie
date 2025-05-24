package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.CustomerSupport;
import com.billetterie.payment.domain.CustomerSupportAlerted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertTransactionFailureCommandHandlerUseCaseTest {

    private AlertTransactionFailureCommandHandler alertTransactionFailureCommandHandler;

    @Mock
    private CustomerSupport customerSupport;

    @BeforeEach
    void setUp() {
        alertTransactionFailureCommandHandler = new AlertTransactionFailureCommandHandler(customerSupport);
    }

    @Nested
    class Handle {
        @Test
        void should_sent_email() {
            var alertTransactionFailureCommand = new AlertTransactionFailureCommand("tx123", "Failed transaction alert", 100f);

            var result = alertTransactionFailureCommandHandler.handle(alertTransactionFailureCommand);

            assertThat(result.firstAs(CustomerSupportAlerted.class)).isNotNull();

            verify(customerSupport).alertTransactionFailure(
                    "tx123",
                    "Failed transaction alert",
                    100f);
        }
    }

    @Test
    void listenTo_should_return_AlertTransactionFailureCommand() {
        assertThat(alertTransactionFailureCommandHandler.listenTo()).isEqualTo(AlertTransactionFailureCommand.class);
    }
}
