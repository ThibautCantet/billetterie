package com.billetterie.payment.infrastructure.controller;

import java.net.URI;

import com.billetterie.payment.common.cqrs.application.CommandController;
import com.billetterie.payment.domain.OrderCreated;
import com.billetterie.payment.domain.OrderNotCreated;
import com.billetterie.payment.domain.PaymentStatus;
import com.billetterie.payment.domain.ValidationRequested;
import com.billetterie.payment.infrastructure.controller.dto.PaymentDto;
import com.billetterie.payment.infrastructure.controller.dto.PaymentResultDto;
import com.billetterie.payment.use_case.PayAndTransformToOrderCommand;
import com.billetterie.payment.use_case.PayAndTransformToOrderCommandHandler;
import com.billetterie.payment.use_case.TransformToOrderCommandHandler;
import com.billetterie.payment.use_case.TransformToOrderCommand;
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
import static com.billetterie.payment.use_case.TransformToOrderCommandHandler.*;

@RestController
@RequestMapping(PaymentController.PATH)
@Slf4j
//TODO extends CommandController
public class PaymentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    public static final String PATH = "/api/payment";
    private final PayAndTransformToOrderCommandHandler payAndTransformToOrderCommandHandler;
    private final TransformToOrderCommandHandler transformToOrderCommandHandler;

    //TODO: replace PayAndTransformToOrder by a command bus factory and call super(commandBusFactory)
    public PaymentController(PayAndTransformToOrderCommandHandler payAndTransformToOrderCommandHandler, TransformToOrderCommandHandler transformToOrderCommandHandler) {
        this.payAndTransformToOrderCommandHandler = payAndTransformToOrderCommandHandler;
        this.transformToOrderCommandHandler = transformToOrderCommandHandler;
    }

    /**
     * Process the payment and transform it into an order.
     * Return data to redirect the user to the bank for payment or to the order confirmation page.
     * @param paymentDto the payment request
     * @return the payment result
     */
    @PostMapping
    public PaymentResultDto processPayment(@RequestBody PaymentDto paymentDto) {
        //TODO: replace use case by a command bus factory to dispatch PayCommand
        var result = payAndTransformToOrderCommandHandler.handle(
                new PayAndTransformToOrderCommand(
                paymentDto.cartDto().id(),
                paymentDto.creditCardDto().number(),
                paymentDto.creditCardDto().expirationDate(),
                paymentDto.creditCardDto().cypher(),
                paymentDto.cartDto().amount()));

        return switch (result.first()) {
            case OrderCreated(
                    PaymentStatus status, String transactionId, String redirectUrl, String orderId, float amount
            ) -> new PaymentResultDto(
                    status,
                    orderId,
                    amount,
                    transactionId,
                    redirectUrl);
            case ValidationRequested(
                    PaymentStatus status, String transactionId, String redirectUrl, Float amount
            ) -> new PaymentResultDto(status,
                    null,
                    amount,
                    transactionId,
                    redirectUrl);
            case null, default -> new PaymentResultDto(FAILED);
        };
    }

    @GetMapping("/cart/confirmation")
    public ResponseEntity bankCallback(
            @RequestParam(name = "transactionId") String transactionId,
            @RequestParam(name = "status") String status,
            @RequestParam(name = "cartId") String cartId,
            @RequestParam(name = "amount") Float amount) {
        PaymentResultDto response;
        var headers = new HttpHeaders();
        if (status.equals("ko")) {
            response = redirectToCartOnError(amount, getErrorCartUrl(cartId, amount), headers);
        } else {
            var result = transformToOrderCommandHandler.handle(new TransformToOrderCommand(transactionId, cartId, amount));

            if (result.first() instanceof OrderNotCreated orderNotCreated) {
                response = redirectToCartOnError(amount, orderNotCreated.redirectUrl(), headers);
            } else if (result.first() instanceof OrderCreated orderCreated) {
                headers.setLocation(URI.create(orderCreated.redirectUrl()));
                response = new PaymentResultDto(
                        SUCCESS,
                        orderCreated.orderId(),
                        orderCreated.amount(),
                        orderCreated.transactionId(),
                        orderCreated.redirectUrl());
                LOGGER.info("Transaction succeeded, redirecting to confirmation page: {} {}", orderCreated.redirectUrl(), response);
            } else {
                response = redirectToCartOnError(amount, getErrorCartUrl(cartId, amount), headers);
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
