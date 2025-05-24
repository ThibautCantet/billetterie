package com.billetterie.payment.listener;

import com.billetterie.payment.domain.PaymentStatus;
import com.billetterie.payment.domain.PaymentSucceeded;
import com.billetterie.payment.use_case.TransformToOrderCommand;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class PaymentSucceededListenerTest {

    private PaymentSucceededListener paymentSucceededListener;

    @BeforeEach
    void setUp() {
        paymentSucceededListener = new PaymentSucceededListener();
    }

    @Test
    void should_return_new_TransformToOrderCommand() {
        var paymentSucceeded = new PaymentSucceeded(PaymentStatus.SUCCESS, "tx123", "cart123", 100.0f);

        var command = paymentSucceededListener.execute(paymentSucceeded);

        assertThat(command)
                .isExactlyInstanceOf(TransformToOrderCommand.class)
                .asInstanceOf(InstanceOfAssertFactories.type(TransformToOrderCommand.class))
                .extracting(TransformToOrderCommand::transactionId,
                            TransformToOrderCommand::cartId,
                            TransformToOrderCommand::amount)
                .containsExactly("tx123", "cart123", 100.0f);
    }

    @Test
    void should_return_PaymentSucceeded() {
        var listenTo = paymentSucceededListener.listenTo();

        assertThat(listenTo).isEqualTo(PaymentSucceeded.class);
    }
}
