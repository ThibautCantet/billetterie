package com.billetterie.payment.domain;

import static com.billetterie.payment.domain.PaymentStatus.*;

public record PayAndTransformToOrderResult(PaymentStatus status, String transactionId, String redirectUrl, String orderId, Float amount,
                                           CartType cartType) {

    public static PayAndTransformToOrderResult pending(String transactionId, String redirectUrl, Float amount, CartType type) {
        return new PayAndTransformToOrderResult(
                PENDING,
                transactionId,
                redirectUrl + "&cartType=" + type.name().toLowerCase(),
                null,
                amount,
                type);
    }

    public static PayAndTransformToOrderResult failed(String transactionId) {
        return new PayAndTransformToOrderResult(
                FAILED,
                transactionId,
                null,
                null,
                0f,
                null);
    }

    public static PayAndTransformToOrderResult failed(String transactionId, String redirectUrl) {
        return new PayAndTransformToOrderResult(
                FAILED,
                transactionId,
                redirectUrl,
                null,
                0f,
                null);
    }

    public static PayAndTransformToOrderResult succeeded(String transactionId, String orderId, Float amount, CartType type, String url) {
        return new PayAndTransformToOrderResult(
                PaymentStatus.SUCCESS,
                transactionId,
                url,
                orderId,
                amount,
                type);
    }
}
