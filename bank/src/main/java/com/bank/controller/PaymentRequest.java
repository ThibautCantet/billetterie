package com.bank.controller;

public record PaymentRequest(
        String cardNumber,
        String expirationDate,
        String cypher,
        String amount
) {
    private static final Float NO_VALIDATION_REQUIRED = 100f;
    private static final float NOT_CANCELABLE_TRANSACTION_AMOUNT = 1000f;

    /*
     * If the amount equals 666, the transaction is rejected
     * @return if the transaction is rejected
     */
    public boolean isRejected() {
        return Float.parseFloat(amount) == 666f;
    }

    /**
     * If the card number contains 200, the validation is not required
     * @return if the validation is not required
     */
    public boolean validationNotRequired() {
        return Float.parseFloat(amount) <= NO_VALIDATION_REQUIRED;
    }

    /**
     * If the card number contains 300, the validation is required
     * @return if the validation is required
     */
    public boolean validationRequired() {
        return !validationNotRequired();
    }

    /**
     * If the amount is greater than 1000, the transaction is not cancelable
     * @return if the transaction is not cancelable
     */
    public boolean notCancelable() {
        return Float.parseFloat(amount) > NOT_CANCELABLE_TRANSACTION_AMOUNT;
    }

    /**
     * If the amount is less than or equal to 1000, the transaction is cancelable
     * @return if the transaction is cancelable
     */
    public boolean cancelable() {
        return !notCancelable();
    }
}
