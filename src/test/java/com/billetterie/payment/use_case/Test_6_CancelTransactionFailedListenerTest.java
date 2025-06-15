package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.CancelTransactionFailed;
import com.billetterie.payment.listener.CancelTransactionFailedListener;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class Test_6_CancelTransactionFailedListenerTest {

    private static final String TRANSACTION_ID = "tx123";
    private static final String CART_ID = "cartId";
    private static final float AMOUNT = 100.0f;

    private CancelTransactionFailedListener cancelTransactionFailedListener;

    @BeforeEach
    void setUp() {
        cancelTransactionFailedListener = new CancelTransactionFailedListener();
    }

    @Test
    void should_return_new_AlertTransactionFailureCommand() {
        var cancelTransactionFailed = new CancelTransactionFailed(TRANSACTION_ID, CART_ID, AMOUNT);

        var command = cancelTransactionFailedListener.handle(cancelTransactionFailed);

        assertThat(command)
                .isExactlyInstanceOf(AlertTransactionFailureCommand.class)
                .asInstanceOf(InstanceOfAssertFactories.type(AlertTransactionFailureCommand.class))
                .extracting(AlertTransactionFailureCommand::transactionId,
                        AlertTransactionFailureCommand::amount)
                .containsExactly(TRANSACTION_ID, AMOUNT);
    }

    @Test
    void should_return_CancelTransactionFailed() {
        var listenTo = cancelTransactionFailedListener.listenTo();

        assertThat(listenTo).isEqualTo(CancelTransactionFailed.class);
    }
}
