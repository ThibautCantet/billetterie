package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.Payment;
import com.billetterie.payment.domain.PaymentStatus;
import com.billetterie.payment.domain.PaymentSucceeded;
import com.billetterie.payment.domain.Transaction;
import com.billetterie.payment.domain.TransactionFailed;
import com.billetterie.payment.domain.ValidationRequested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PayCommandHandlerTest {
    private  static final String CART_ID = "cartId";
    private  static final String CARD_NUMBER = "1234 5678 9012 3456";
    private  static final String EXPIRATION_DATE = "12/25";
    private  static final String CYPHER = "999";
    private  static final float AMOUNT = 50.0f;
    private  static final String TRANSACTION_ID = "tx1";
    private  static final String REDIRECTION_URL = "/3ds";

    private PayCommandHandler payCommandHandler;

    @Mock
    private Bank bank;

    @BeforeEach
    void setUp() {
        payCommandHandler = new PayCommandHandler(bank);
    }

    @Nested
    class Handle {
        @Test
        void should_return_ValidationRequested_when_pending_transaction() {
            var command = new PayCommand(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT);
            var transaction = new Transaction(TRANSACTION_ID, PaymentStatus.PENDING, REDIRECTION_URL);
            var payment = new Payment(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT);
            when(bank.pay(payment)).thenReturn(transaction);

            var result = payCommandHandler.handle(command);

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
            var payment = new Payment(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT);
            when(bank.pay(payment)).thenReturn(transaction);

            var result = payCommandHandler.handle(command);

            assertThat(result.firstAs(TransactionFailed.class))
                    .extracting(TransactionFailed::status,
                            TransactionFailed::id)
                    .containsExactly(PaymentStatus.FAILED, TRANSACTION_ID);
        }

        @Test
        void should_return_PaymentSucceeded_when_transaction_succeeded() {
            var command = new PayCommand(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT);
            var transaction = new Transaction(TRANSACTION_ID, PaymentStatus.SUCCESS, null);
            var payment = new Payment(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT);
            when(bank.pay(payment)).thenReturn(transaction);

            var result = payCommandHandler.handle(command);

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
        assertThat(payCommandHandler.listenTo()).isEqualTo(PayCommand.class);
    }
}
