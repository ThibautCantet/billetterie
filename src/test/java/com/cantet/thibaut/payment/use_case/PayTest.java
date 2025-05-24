package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.domain.Bank;
import com.cantet.thibaut.payment.domain.Payment;
import com.cantet.thibaut.payment.domain.PaymentStatus;
import com.cantet.thibaut.payment.domain.PaymentSucceeded;
import com.cantet.thibaut.payment.domain.Transaction;
import com.cantet.thibaut.payment.domain.TransactionFailed;
import com.cantet.thibaut.payment.domain.ValidationRequested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PayTest {
    private  static final String CART_ID = "cartId";
    private  static final String CARD_NUMBER = "1234 5678 9012 3456";
    private  static final String EXPIRATION_DATE = "12/25";
    private  static final String CYPHER = "999";
    private  static final float AMOUNT = 50.0f;
    private  static final String TRANSACTION_ID = "tx1";
    private  static final String REDIRECTION_URL = "/3ds";

    private Pay pay;

    @Mock
    private Bank bank;

    @BeforeEach
    void setUp() {
        pay = new Pay(bank);
    }

    @Nested
    class Execute {
        @Test
        void should_return_ValidationRequested_when_pending_transaction() {
            var command = new PayCommand(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT);
            var transaction = new Transaction(TRANSACTION_ID, PaymentStatus.PENDING, REDIRECTION_URL);
            var payment = new Payment(CARD_NUMBER, EXPIRATION_DATE, CYPHER, CART_ID, AMOUNT);
            when(bank.pay(payment)).thenReturn(transaction);

            var result = pay.execute(command);

            assertThat(result.firstAs(ValidationRequested.class))
                    .extracting(ValidationRequested::status,
                            ValidationRequested::transactionId,
                            ValidationRequested::redirectUrl,
                            ValidationRequested::amount)
                    .containsExactly(PaymentStatus.PENDING, TRANSACTION_ID, REDIRECTION_URL, AMOUNT);
        }

        @Test
        void should_return_TransactionFailed_when_failed_transaction() {
            var command = new PayCommand(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT);
            var transaction = new Transaction(TRANSACTION_ID, PaymentStatus.FAILED, null);
            var payment = new Payment(CARD_NUMBER, EXPIRATION_DATE, CYPHER, CART_ID, AMOUNT);
            when(bank.pay(payment)).thenReturn(transaction);

            var result = pay.execute(command);

            assertThat(result.firstAs(TransactionFailed.class))
                    .extracting(TransactionFailed::status,
                            TransactionFailed::id)
                    .containsExactly(PaymentStatus.FAILED, TRANSACTION_ID);
        }

        @Test
        void should_return_PaymentSucceeded_when_transaction_succeeded() {
            var command = new PayCommand(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT);
            var transaction = new Transaction(TRANSACTION_ID, PaymentStatus.SUCCESS, null);
            var payment = new Payment(CARD_NUMBER, EXPIRATION_DATE, CYPHER, CART_ID, AMOUNT);
            when(bank.pay(payment)).thenReturn(transaction);

            var result = pay.execute(command);

            assertThat(result.firstAs(PaymentSucceeded.class))
                    .extracting(PaymentSucceeded::status,
                            PaymentSucceeded::transactionId,
                            PaymentSucceeded::cartId,
                            PaymentSucceeded::amount)
                    .containsExactly(PaymentStatus.SUCCESS, TRANSACTION_ID, CART_ID, AMOUNT);
        }
    }

    @Test
    void listenTo_should_return_PayCommand() {
        assertThat(pay.listenTo()).isEqualTo(PayCommand.class);
    }
}
