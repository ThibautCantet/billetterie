package com.cantet.thibaut.payment.infrastructure.controller;

import com.cantet.thibaut.payment.domain.PayAndTransformToOrderResult;
import com.cantet.thibaut.payment.infrastructure.controller.dto.PaymentResultDto;
import com.cantet.thibaut.payment.infrastructure.controller.dto.PaymentDto;
import com.cantet.thibaut.payment.use_case.PayAndTransformToOrder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PaymentController.PATH)
public class PaymentController {
    public static final String PATH = "/api/payment";
    private final PayAndTransformToOrder payAndTransformToOrder;

    public PaymentController(PayAndTransformToOrder payAndTransformToOrder) {
        this.payAndTransformToOrder = payAndTransformToOrder;
    }


    @PostMapping
    public PaymentResultDto processPayment(@RequestBody PaymentDto paymentDto) {
        PayAndTransformToOrderResult result = payAndTransformToOrder.execute(
                paymentDto.cartDto().id(),
                paymentDto.creditCardDto().number(),
                paymentDto.creditCardDto().expirationDate(),
                paymentDto.creditCardDto().cypher(),
                paymentDto.cartDto().amount());
        return new PaymentResultDto(
                result.status(),
                result.orderId(),
                result.amount(),
                result.transactionId(),
                result.redirectUrl());
    }
}
