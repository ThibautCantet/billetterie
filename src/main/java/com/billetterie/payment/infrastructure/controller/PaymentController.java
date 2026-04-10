package com.billetterie.payment.infrastructure.controller;

import java.net.URI;

import com.billetterie.payment.common.cqrs.application.CommandController;
import com.billetterie.payment.common.cqrs.middleware.command.CommandBusFactory;
import com.billetterie.payment.domain.OrderCreated;
import com.billetterie.payment.domain.OrderNotCreated;
import com.billetterie.payment.domain.ValidationRequested;
import com.billetterie.payment.infrastructure.controller.dto.PaymentDto;
import com.billetterie.payment.infrastructure.controller.dto.PaymentResultDto;
import com.billetterie.payment.choregraphy.handler.PayCommand;
import com.billetterie.payment.choregraphy.handler.TransformToOrderCommand;
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

@RestController
@RequestMapping(PaymentController.PATH)
@Slf4j
public class PaymentController extends CommandController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    public static final String PATH = "/api/payment";

    public PaymentController(CommandBusFactory commandBusFactory) {
        super(commandBusFactory);
    }

    /**
     * Process the payment and transform it into an order.
     * Return data to redirect the user to the bank for payment or to the order confirmation page.
     * @param paymentDto the payment request
     * @return the payment result
     */
    @PostMapping
    public PaymentResultDto processPayment(@RequestBody PaymentDto paymentDto) {
        var result = getCommandBus().dispatch(new PayCommand(
                paymentDto.cartDto().id(),
                paymentDto.creditCardDto().number(),
                paymentDto.creditCardDto().expirationDate(),
                paymentDto.creditCardDto().cypher(),
                paymentDto.cartDto().amount(),
                paymentDto.email()));
        var orderCreated = (OrderCreated) result.firstAs(OrderCreated.class);
        if (orderCreated != null) {
            return new PaymentResultDto(
                    SUCCESS,
                    orderCreated.orderId(),
                    orderCreated.amount(),
                    orderCreated.transactionId(),
                    orderCreated.redirectUrl());
        }

        var validationRequested = (ValidationRequested) result.firstAs(ValidationRequested.class);
        if (validationRequested != null) {
            return new PaymentResultDto(
                    validationRequested.status(),
                    null,
                    validationRequested.amount(),
                    validationRequested.transactionId(),
                    validationRequested.redirectUrl());
        }

        return new PaymentResultDto(FAILED);
    }

    @GetMapping("/cart/confirmation")
    public ResponseEntity<PaymentResultDto> bankCallback(
            @RequestParam(name = "transactionId") String transactionId,
            @RequestParam(name = "status") String status,
            @RequestParam(name = "cartId") String cartId,
            @RequestParam(name = "amount") Float amount,
            @RequestParam(name = "email") String email) {
        PaymentResultDto response = null;
        var headers = new HttpHeaders();
        if (status.equals("ko")) {
            response = redirectToCartOnError(amount, getErrorCartUrl(cartId, amount), headers);
        } else {
            var result = getCommandBus().dispatch(new TransformToOrderCommand(transactionId, cartId, amount, email));

            var orderNotCreated = (OrderNotCreated) result.findFirst(OrderNotCreated.class).orElse(null);
            var orderCreated = (OrderCreated) result.findFirst(OrderCreated.class).orElse(null);

            if (orderNotCreated != null) {
                response = redirectToCartOnError(amount, orderNotCreated.redirectUrl(), headers);
            } else if (orderCreated != null) {
                headers.setLocation(URI.create(orderCreated.redirectUrl()));
                response = new PaymentResultDto(
                        SUCCESS,
                        orderCreated.orderId(),
                        orderCreated.amount(),
                        orderCreated.transactionId(),
                        orderCreated.redirectUrl());
                LOGGER.info("Transaction succeeded, redirecting to confirmation page: {} {}", orderCreated.redirectUrl(), response);
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

    private static String getErrorCartUrl(String cartId, float amount) {
        return "/cart?error=true&cartId=" + cartId + "&amount=" + amount;
    }
}
