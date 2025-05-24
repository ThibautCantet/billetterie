package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.domain.CustomerSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertTransactionFailureTest {

    private AlertTransactionFailure alertTransactionFailure;

    @Mock
    private CustomerSupport customerSupport;

    @BeforeEach
    void setUp() {
        alertTransactionFailure = new AlertTransactionFailure(customerSupport);
    }

    @Nested
    class Execute {
        @Test
        void should_sent_email() {
            var alertTransactionFailureCommand = new AlertTransactionFailureCommand("tx123", "Failed transaction alert", 100f);

            alertTransactionFailure.execute(alertTransactionFailureCommand);

            verify(customerSupport).alertTransactionFailure(
                    "tx123",
                    "Failed transaction alert",
                    100f);
        }
    }

    @Test
    void listenTo_should_return_AlertTransactionFailureCommand() {
        assertThat(alertTransactionFailure.listenTo()).isEqualTo(AlertTransactionFailureCommand.class);
    }
}
