package com.billetterie.payment.infrastructure.controller;

import java.net.URI;

import com.billetterie.payment.domain.PayAndTransformToOrderResult;
import com.billetterie.payment.domain.TransformToOrderResult;
import com.billetterie.payment.domain.TransformToOrderStatus;
import com.billetterie.payment.infrastructure.controller.dto.PaymentDto;
import com.billetterie.payment.infrastructure.controller.dto.PaymentResultDto;
import com.billetterie.payment.use_case.PayAndTransformToOrder;
import com.billetterie.payment.use_case.TransformToOrder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.billetterie.payment.domain.PaymentStatus.*;
import static com.billetterie.payment.use_case.TransformToOrder.*;

@RestController
@RequestMapping(PaymentController.PATH)
@Slf4j
public class PaymentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    public static final String PATH = "/api/payment";
    private final PayAndTransformToOrder payAndTransformToOrder;
    private final TransformToOrder transformToOrder;

    public PaymentController(PayAndTransformToOrder payAndTransformToOrder, TransformToOrder transformToOrder) {
        this.payAndTransformToOrder = payAndTransformToOrder;
        this.transformToOrder = transformToOrder;
    }

    /**
     * Process the payment and transform it into an order.
     * Return data to redirect the user to the bank for payment or to the order confirmation page.
     * @param paymentDto the payment request
     * @return the payment result
     */
    @PostMapping
    public PaymentResultDto processPayment(@RequestBody PaymentDto paymentDto) {
        PayAndTransformToOrderResult result = payAndTransformToOrder.execute(
                paymentDto.cartDto().id(),
                paymentDto.creditCardDto().number(),
                paymentDto.creditCardDto().expirationDate(),
                paymentDto.creditCardDto().cypher(),
                paymentDto.cartDto().amount());

        if (result.status() == FAILED) {
            return new PaymentResultDto(result.status());
        }

        return new PaymentResultDto(
                result.status(),
                result.orderId(),
                result.amount(),
                result.transactionId(),
                result.redirectUrl());
    }

    @GetMapping("/cart/confirmation")
    public ResponseEntity bankCallback(
            @RequestParam(name = "transactionId") String transactionId,
            @RequestParam(name = "status") String status,
            @RequestParam(name = "cartId") String cartId,
            @RequestParam(name = "amount") Float amount) {
        PaymentResultDto response;
        TransformToOrderResult result;
        var headers = new HttpHeaders();
        if (status.equals("ko")) {
            response = redirectToCartOnError(amount, getErrorCartUrl(cartId, amount), headers);
        } else {
            result = transformToOrder.execute(transactionId, cartId, amount);

            if (result.status() == TransformToOrderStatus.FAILED) {
                response = redirectToCartOnError(amount, result.redirectUrl(), headers);
            } else {
                headers.setLocation(URI.create(result.redirectUrl()));
                response = new PaymentResultDto(
                        SUCCESS,
                        result.orderId(),
                        result.amount(),
                        result.transactionId(),
                        result.redirectUrl());
                LOGGER.info("Transaction succeeded, redirecting to confirmation page: {} {}", result.redirectUrl(), response);
            }
        }
        return new ResponseEntity<>(response, headers, HttpStatusCode.valueOf(301));
    }

    private static PaymentResultDto redirectToCartOnError(Float amount, String cartUrl, HttpHeaders headers) {
        var response = new PaymentResultDto(FAILED, null, amount, null, cartUrl);
        headers.setLocation(URI.create(cartUrl));

        LOGGER.error("Transaction failed, redirecting to cart with error: {} {}", cartUrl, response);
        return response;
    }
}
