package com.bank.controller;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BankController.PATH)
public class BankController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankController.class);

    static final String PATH = "/api/bank";
    public static final String NOT_CANCELABLE_PATTERN = "500";

    /**
     * Pay the given payment request.
     *
     * @param request the payment request
     * @return the transaction ok; ko; pending; rejected
     */
    @PostMapping("/payments")
    public TransactionResponse pay(@RequestBody PaymentRequest request) {
        if (request.isRejected()) {
            return TransactionResponse.rejected();
        }
        if (request.validationNotRequired()) {
            if (request.cardNumber().contains(NOT_CANCELABLE_PATTERN)) {
                return TransactionResponse.withoutValidationAndNotCancelable();
            } else {
                return TransactionResponse.withoutValidationAndCancelable();
            }
        } else if (request.validationRequired()) {
            return TransactionResponse.pending(request);
        }
        return TransactionResponse.ko();
    }

    @DeleteMapping("/payments/{transactionId}")
    public Boolean cancel(@PathVariable(name = "transactionId") String transactionId,
                          @RequestParam(name = "amount") Float amount) {
        boolean canceled = amount < 1000f && !transactionId.contains(NOT_CANCELABLE_PATTERN);
        if (canceled) {
            LOGGER.info("Transaction {} of {}€ canceled", transactionId, amount);
        } else {
            LOGGER.warn("Transaction {} of {}€ not canceled", transactionId, amount);
        }
        return canceled;
    }

    @GetMapping("/payments/3ds")
    public ResponseEntity<Object> pay3ds(
            @RequestParam(name = "status") String status,
            @RequestParam(name = "cartId") String cartId,
            @RequestParam(name = "amount") Float amount,
            @RequestParam(name = "transactionId") String transactionId) {
        var headers = new HttpHeaders();
        headers.setLocation(URI.create(
                String.format("http://localhost:8080/api/payment/cart/confirmation?transactionId=%s&status=ok&cartId=%s&amount=%s",
                        transactionId, cartId, amount)));
        return new ResponseEntity<>(headers, HttpStatusCode.valueOf(301));
    }
}
