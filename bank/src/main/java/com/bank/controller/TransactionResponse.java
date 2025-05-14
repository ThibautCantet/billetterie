package com.bank.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bank.controller.BankController.*;

public record TransactionResponse(String id, String status, String redirectionUrl) {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionResponse.class);

    private static final String CANCELABLE_TRANSACTION_ID = "123456789";
    private static final String NOT_CANCELABLE_TRANSACTION_ID = "1123456789";

    public static TransactionResponse withoutValidationAndCancelable() {
        LOGGER.info("Transaction without validation and cancelable");
        return new TransactionResponse(CANCELABLE_TRANSACTION_ID, "ok", null);
    }

    public static TransactionResponse pending(PaymentRequest request) {
        String transactionId;
        if (request.cardNumber().contains("11")) {
            LOGGER.info("Transaction with validation and not cancelable, card number: {}", request.cardNumber());
            transactionId = NOT_CANCELABLE_TRANSACTION_ID;
        } else {
            LOGGER.info("Transaction with validation and cancelable, card number: {}", request.cardNumber());
            transactionId = CANCELABLE_TRANSACTION_ID;
        }
        return new TransactionResponse(transactionId, "PENDING", PATH + "/payments/3ds");
    }

    public static TransactionResponse ko() {
        LOGGER.info("Transaction ko");
        return new TransactionResponse("312354645", "ko", null);
    }
}
