package com.billetterie.payment.infrastructure.controller;

import java.net.URI;

import com.billetterie.payment.common.cqrs.application.CommandController;
import com.billetterie.payment.common.cqrs.middleware.command.CommandBusFactory;
import com.billetterie.payment.domain.CartType;
import com.billetterie.payment.domain.ClassicOrderCreated;
import com.billetterie.payment.domain.ClassicOrderNotCreated;
import com.billetterie.payment.domain.OrderCreated;
import com.billetterie.payment.domain.PanierReserveCreated;
import com.billetterie.payment.domain.PanierReserveNotCreated;
import com.billetterie.payment.domain.PaymentStatus;
import com.billetterie.payment.domain.ValidationRequested;
import com.billetterie.payment.infrastructure.controller.dto.PaymentDto;
import com.billetterie.payment.infrastructure.controller.dto.PaymentResultDto;
import com.billetterie.payment.use_case.PayCommand;
import com.billetterie.payment.use_case.PayCommandHandler;
import com.billetterie.payment.use_case.TransformToOrderCommand;
import com.billetterie.payment.use_case.TransformToOrderCommandHandler;
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
public class PaymentController extends CommandController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    public static final String PATH = "/api/payment";
    private final PayCommandHandler payCommandHandler;
    private final TransformToOrderCommandHandler transformToOrderCommandHandler;

    public PaymentController(CommandBusFactory factory,
                             PayCommandHandler payCommandHandler,
                             TransformToOrderCommandHandler transformToOrderCommandHandler) {
        super(factory);
        this.payCommandHandler = payCommandHandler;
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
        var result = payCommandHandler.handle(
                new PayCommand(
                paymentDto.cartDto().id(),
                paymentDto.creditCardDto().number(),
                paymentDto.creditCardDto().expirationDate(),
                paymentDto.creditCardDto().cypher(),
                paymentDto.cartDto().amount(),
                paymentDto.cartDto().type()));

        return switch (result.first()) {
            case ClassicOrderCreated(
                    PaymentStatus status, String transactionId, String redirectUrl, String orderId, float amount
            ) -> new PaymentResultDto(
                    status,
                    orderId,
                    amount,
                    transactionId,
                    redirectUrl);
            case PanierReserveCreated(
                    PaymentStatus status, String transactionId, String redirectUrl, String orderId, float amount
            ) -> new PaymentResultDto(
                    status,
                    orderId,
                    amount,
                    transactionId,
                    redirectUrl);
            case ValidationRequested(
                    PaymentStatus status, String transactionId, String redirectUrl, Float amount, CartType cartType
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
            @RequestParam(name = "amount") Float amount,
            @RequestParam(name = "cartType") CartType type) {
        PaymentResultDto response;
        var headers = new HttpHeaders();
        String url;
        if (type == CartType.CLASSIC) {
            url = getErrorCartUrl(cartId, amount);
        } else {
            url = getErrorUrl(cartId, amount);
        }
        if (status.equals("ko")) {
            if (type == CartType.CLASSIC) {
                url = getErrorCartUrl(cartId, amount);
            } else {
                url = getErrorUrl(cartId, amount);
            }
            response = redirectToCartOnError(amount, url, headers);
        } else {
            //TODO: dispatch TransformToOrderCommand
            var result = transformToOrderCommandHandler.handle(new TransformToOrderCommand(transactionId, cartId, amount, type));

            switch (result.first()) {
                case ClassicOrderNotCreated orderNotCreated ->
                        response = redirectToCartOnError(amount, orderNotCreated.redirectUrl(), headers);
                case PanierReserveNotCreated orderNotCreated ->
                        response = redirectToCartOnError(amount, orderNotCreated.redirectUrl(), headers);
                case PanierReserveCreated panierReserveCreated -> {
                    headers.setLocation(URI.create(panierReserveCreated.redirectUrl()));
                    response = buildOrderCreatedResponse(panierReserveCreated);
                    LOGGER.info("Transaction succeeded, redirecting to my orders page: {} {}", panierReserveCreated.redirectUrl(), response);
                }
                case ClassicOrderCreated classicOrderCreated -> {
                    headers.setLocation(URI.create(classicOrderCreated.redirectUrl()));
                    response = buildOrderCreatedResponse(classicOrderCreated);
                    LOGGER.info("Transaction succeeded, redirecting to confirmation page: {} {}", classicOrderCreated.redirectUrl(), response);
                }
                case null, default -> response = redirectToCartOnError(amount, url, headers);
            }
        }
        return new ResponseEntity<>(response, headers, HttpStatusCode.valueOf(301));
    }

    private static PaymentResultDto buildOrderCreatedResponse(OrderCreated orderCreated) {
        return new PaymentResultDto(
                PaymentStatus.SUCCESS,
                orderCreated.orderId(),
                orderCreated.amount(),
                orderCreated.transactionId(),
                orderCreated.redirectUrl());
    }

    private static PaymentResultDto redirectToCartOnError(Float amount, String cartUrl, HttpHeaders headers) {
        var response = new PaymentResultDto(FAILED, null, amount, null, cartUrl);
        headers.setLocation(URI.create(cartUrl));

        LOGGER.error("Transaction failed, redirecting to cart with error: {} {}", cartUrl, response);
        return response;
    }
}
