package com.bank.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record TransactionResponse(String id, String status, String redirectionUrl) {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionResponse.class);

    private static final String CANCELABLE_TRANSACTION_ID = "123456789";
    private static final String NOT_CANCELABLE_TRANSACTION_ID = "1123456500";
    public static final String TRANSACTION_KO = "312354645";
    public static final String TRANSACTION_REJECTED = "654654654";

    public static TransactionResponse withoutValidationAndCancelable() {
        LOGGER.info("Transaction {} without validation and cancelable", CANCELABLE_TRANSACTION_ID);
        return new TransactionResponse(CANCELABLE_TRANSACTION_ID, "ok", null);
    }

    public static TransactionResponse withoutValidationAndNotCancelable() {
        LOGGER.info("Transaction {} without validation and not cancelable", NOT_CANCELABLE_TRANSACTION_ID);
        return new TransactionResponse(NOT_CANCELABLE_TRANSACTION_ID, "ok", null);
    }

    public static TransactionResponse pending(PaymentRequest request) {
        String transactionId;
        if (request.notCancelable()) {
            LOGGER.info("Transaction {} with validation and not cancelable, card number: {}", NOT_CANCELABLE_TRANSACTION_ID, request.cardNumber());
            transactionId = NOT_CANCELABLE_TRANSACTION_ID;
        } else {
            LOGGER.info("Transaction {} with validation and cancelable, card number: {}", CANCELABLE_TRANSACTION_ID, request.cardNumber());
            transactionId = CANCELABLE_TRANSACTION_ID;
        }
        var transaction = new TransactionResponse(transactionId,
                "PENDING",
                String.format("http://localhost:8082/payments/3ds?transactionId=%s&status=%s&cartId=%s&amount=%s",
                        transactionId,
                        "ok",
                        request.cartId(),
                        request.amount()));
        LOGGER.info("Transaction pending {}", transaction);
        return transaction;
    }

    public static TransactionResponse ko() {
        LOGGER.info("Transaction {} ko", TRANSACTION_KO);
        return new TransactionResponse(TRANSACTION_KO, "ko", null);
    }

    public static TransactionResponse rejected() {
        LOGGER.info("Transaction {} rejected", TRANSACTION_REJECTED);
        return new TransactionResponse(TRANSACTION_REJECTED, "ko", null);
    }
}
