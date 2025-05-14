package com.bank.controller;

public record PaymentRequest(
        String cardNumber,
        String expirationDate,
        String cypher,
        String amount
) {
    private static final String NO_VALIDATION_REQUIRED = "200";
    private static final String VALIDATION_REQUIRED = "300";

    /**
     * If the card number contains 200, the validation is not required
     * @return if the validation is not required
     */
    public boolean validationNotRequired() {
        return cardNumber.contains(NO_VALIDATION_REQUIRED);
    }

    /**
     * If the card number contains 300, the validation is required
     * @return if the validation is required
     */
    public boolean validationRequired() {
        return cardNumber.contains(VALIDATION_REQUIRED);
    }
}
