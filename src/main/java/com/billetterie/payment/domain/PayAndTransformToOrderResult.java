package com.billetterie.payment.domain;

import static com.billetterie.payment.domain.PaymentStatus.*;

public record PayAndTransformToOrderResult(PaymentStatus status, String transactionId, String redirectUrl, String orderId, Float amount) {

    public static PayAndTransformToOrderResult pending(String transactionId, String redirectUrl, Float amount) {
        return new PayAndTransformToOrderResult(
                PENDING,
                transactionId,
                redirectUrl,
                null,
                amount);
    }

    public static PayAndTransformToOrderResult failed(String transactionId) {
        return new PayAndTransformToOrderResult(
                FAILED,
                transactionId,
                null,
                null,
                0f);
    }

    public static PayAndTransformToOrderResult failed(String transactionId, String redirectUrl) {
        return new PayAndTransformToOrderResult(
                FAILED,
                transactionId,
                redirectUrl,
                null,
                0f);
    }

    public static PayAndTransformToOrderResult succeeded(String transactionId, String orderId, Float amount) {
        return new PayAndTransformToOrderResult(
                PaymentStatus.SUCCESS,
                transactionId,
                String.format("/confirmation/%s?amount=%s", orderId, amount),
                orderId,
                amount);
    }
}
