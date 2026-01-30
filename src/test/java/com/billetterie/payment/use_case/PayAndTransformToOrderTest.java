package com.billetterie.payment.use_case;


import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.ConfirmationService;
import com.billetterie.payment.domain.CustomerSupport;
import com.billetterie.payment.domain.Order;
import com.billetterie.payment.domain.Orders;
import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.Payment;
import com.billetterie.payment.domain.PaymentStatus;
import com.billetterie.payment.domain.Transaction;
import com.billetterie.payment.domain.TransactionFailed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayAndTransformToOrderTest {

    private static final String CART_ID = "123456";
    private static final String ORDER_ID = "654654";
    private static final String CARD_NUMBER = "1234567890123456";
    private static final String EXPIRATION_DATE = "12/27";
    private static final String CYPHER = "123";
    private static final float AMOUNT = 100.0f;
    private static final String EMAIL = "client@mail.com";
    private static final String TRANSACTION_ID = "324234243234";

    private PayAndTransformToOrder payAndTransformToOrder;
    @Mock
    private Bank bank;

    @Mock
    private Orders orders;
    @Mock
    private CustomerSupport customerSupport;
    @Mock
    private ConfirmationService confirmationService;

    @BeforeEach
    void setUp() {
        payAndTransformToOrder = new PayAndTransformToOrder(bank, new TransformToOrder(orders, bank, customerSupport, confirmationService));
    }

    @Test
    public void should_return_ValidationRequested_when_pending_transaction() {
        // given
        var transactionToValidate = new Transaction(TRANSACTION_ID, PaymentStatus.PENDING, "/3ds");

        when(bank.pay(new Payment(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT, EMAIL)))
                .thenReturn(transactionToValidate);

        // when
        //TODO(1): encapsuler les paramètres du use case dans un objet command PayAndTransformToOrderCommand
        var result = payAndTransformToOrder.execute(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT, EMAIL);

        // then
        assertThat(result).extracting(PayAndTransformToOrderResult::status,
                        PayAndTransformToOrderResult::transactionId,
                        PayAndTransformToOrderResult::redirectUrl,
                        PayAndTransformToOrderResult::amount,
                        PayAndTransformToOrderResult::email)
                .containsExactly(PaymentStatus.PENDING, TRANSACTION_ID, "/3ds", AMOUNT, EMAIL);

        //TODO(); remplacer l'assertion de result par l'assertion une fois que la méthode retourne des events
        //assertThat(result.firstAs(ValidationRequested.class))
        //        .extracting(ValidationRequested::status,
        //                ValidationRequested::transactionId,
        //                ValidationRequested::redirectUrl,
        //                ValidationRequested::amount)
        //        .containsExactly(PaymentStatus.PENDING, TRANSACTION_ID, REDIRECTION_URL, AMOUNT);
    }

    @Test
    void should_return_TransactionFailed_when_failed_transaction() {
        // given
        var failedTransaction = new Transaction(TRANSACTION_ID, PaymentStatus.FAILED, null);

        when(bank.pay(new Payment(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT, EMAIL)))
                .thenReturn(failedTransaction);

        // when
        var result = payAndTransformToOrder.execute(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT, EMAIL);

        // then
        //TODO(); remplacer l'assertion de result par l'assertion une fois que la méthode retourne des events
        assertThat(result)
                .isInstanceOf(TransactionFailed.class);
        //assertThat(result.firstAs(TransactionFailed.class))
        //        .extracting(TransactionFailed::status,
        //                TransactionFailed::id)
        //        .containsExactly(PaymentStatus.FAILED, TRANSACTION_ID);
    }

    @Test
    public void should_return_PaymentSucceeded_when_transaction_succeeded() {
        // given
        var succeededTransaction = new Transaction(TRANSACTION_ID, PaymentStatus.SUCCESS, null);

        when(bank.pay(new Payment(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT, EMAIL)))
                .thenReturn(succeededTransaction);

        var order = new Order(ORDER_ID, AMOUNT);
        when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(order);

        // when
        var result = payAndTransformToOrder.execute(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT, EMAIL);

        // then
        assertThat(result).extracting(PayAndTransformToOrderResult::status,
                        PayAndTransformToOrderResult::transactionId,
                        PayAndTransformToOrderResult::orderId,
                        PayAndTransformToOrderResult::redirectUrl,
                        PayAndTransformToOrderResult::amount)
                .containsExactly(PaymentStatus.SUCCESS, "324234243234", ORDER_ID, "/confirmation/654654?amount=100.0", AMOUNT);

        //TODO(); remplacer l'assertion de result par l'assertion une fois que la méthode retourne des events
        //assertThat(result.firstAs(PaymentSucceeded.class))
        //        .extracting(PaymentSucceeded::status,
        //                PaymentSucceeded::transactionId,
        //                PaymentSucceeded::cartId,
        //                PaymentSucceeded::amount)
        //        .containsExactly(PaymentStatus.SUCCESS, TRANSACTION_ID, CART_ID, AMOUNT);
        verify(confirmationService, never()).send(any(), any(), anyFloat());
    }

    @Test
    public void should_return_failed_and_cancel_transaction_when_payment_success_but_transform_to_order_fails() {
        // given
        var succeededTransaction = new Transaction(TRANSACTION_ID, PaymentStatus.SUCCESS, null);

        when(bank.pay(new Payment(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT, EMAIL)))
                .thenReturn(succeededTransaction);

        var failedOrder = new Order(null, 0f);
        when(orders.transformToOrder(CART_ID, AMOUNT)).thenReturn(failedOrder);

        when(bank.cancel(TRANSACTION_ID, AMOUNT)).thenReturn(true);

        // when
        var result = payAndTransformToOrder.execute(CART_ID, CARD_NUMBER, EXPIRATION_DATE, CYPHER, AMOUNT, EMAIL);

        // then
        assertThat(result).extracting(PayAndTransformToOrderResult::status,
                        PayAndTransformToOrderResult::transactionId,
                        PayAndTransformToOrderResult::redirectUrl)
                .containsExactly(PaymentStatus.FAILED,
                        TRANSACTION_ID,
                        "/cart?error=true&cartId=123456&amount=100.0");

        verify(bank, never()).cancel(any(), anyFloat());

        verify(customerSupport, never()).alertTransactionFailure(any(), any(), any());

        verify(confirmationService, never()).send(any(), any(), anyFloat());
    }

    @Test
    void listenTo_should_return_PayCommand() {
        //assertThat(payCommandHandler.listenTo()).isEqualTo(PayCommand.class);
    }
}
