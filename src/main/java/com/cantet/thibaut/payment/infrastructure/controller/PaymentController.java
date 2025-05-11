package com.cantet.thibaut.payment.infrastructure.controller;

import com.cantet.thibaut.payment.domain.PayAndTransformToOrderResult;
import com.cantet.thibaut.payment.domain.TransformToOrderStatus;
import com.cantet.thibaut.payment.infrastructure.controller.dto.PaymentDto;
import com.cantet.thibaut.payment.infrastructure.controller.dto.PaymentResultDto;
import com.cantet.thibaut.payment.use_case.PayAndTransformToOrder;
import com.cantet.thibaut.payment.use_case.TransformToOrder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.cantet.thibaut.payment.domain.PaymentStatus.*;

@RestController
@RequestMapping(PaymentController.PATH)
public class PaymentController {
    public static final String PATH = "/api/payment";
    private final PayAndTransformToOrder payAndTransformToOrder;
    private final TransformToOrder transformToOrder;

    public PaymentController(PayAndTransformToOrder payAndTransformToOrder, TransformToOrder transformToOrder) {
        this.payAndTransformToOrder = payAndTransformToOrder;
        this.transformToOrder = transformToOrder;
    }


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
    public PaymentResultDto bankCallback(
            @RequestParam(name = "transactionId") String transactionId,
            @RequestParam(name = "status") String status,
            @RequestParam(name = "cartId") String cartId,
            @RequestParam(name = "amount") Float amount) {
        var result = transformToOrder.execute(transactionId, cartId, amount);

        if (result.status() == TransformToOrderStatus.FAILED) {
            return new PaymentResultDto(FAILED, null, amount, null, "/cart?error=true&cartId=" + cartId + "&amount=" + amount);
        }
        return null;
    }
}
