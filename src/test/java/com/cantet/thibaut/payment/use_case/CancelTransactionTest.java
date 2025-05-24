package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.domain.Bank;
import com.cantet.thibaut.payment.domain.CancelTransactionFailed;
import com.cantet.thibaut.payment.domain.CancelTransactionSucceeded;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelTransactionTest {

    private static final String TRANSACTION_ID = "tx123";
    private static final float AMOUNT = 100f;
    private static final String CART_ID = "cartId";

    private CancelTransaction cancelTransaction;

    @Mock
    private Bank bank;

    @BeforeEach
    void setUp() {
        cancelTransaction = new CancelTransaction(bank);
    }

    @Nested
    class Execute {
        @Test
        void should_return_Succeeded_when_transaction_canceled() {
            var command = new CancelTransactionCommand(TRANSACTION_ID, CART_ID, AMOUNT);
            when(bank.cancel(TRANSACTION_ID, AMOUNT)).thenReturn(true);

            var response = cancelTransaction.execute(command);

            assertThat(response.firstAs(CancelTransactionSucceeded.class))
                    .extracting(CancelTransactionSucceeded::transactionId)
                    .isEqualTo(TRANSACTION_ID);
        }

        @Test
        void should_return_Failed_when_transaction_not_canceled() {
            var command = new CancelTransactionCommand(TRANSACTION_ID, CART_ID, AMOUNT);
            when(bank.cancel(TRANSACTION_ID, AMOUNT)).thenReturn(false);

            var response = cancelTransaction.execute(command);

            assertThat(response.firstAs(CancelTransactionFailed.class))
                    .extracting(CancelTransactionFailed::transactionId,
                                CancelTransactionFailed::cartId,
                                CancelTransactionFailed::amount)
                    .containsExactly(TRANSACTION_ID, CART_ID, AMOUNT);
        }
    }

    @Test
    void listenTo_should_return_CancelTransactionCommand() {
        assertThat(cancelTransaction.listenTo()).isEqualTo(CancelTransactionCommand.class);
    }
}
