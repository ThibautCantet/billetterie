package com.backofficebilletterie.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrdersController.class);

    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest) {
        if (orderRequest.cartId().contains("200")) {
            LOGGER.info("Order created successfully for cartId: {}", orderRequest.cartId());
            return new OrderResponse(orderRequest.cartId(), orderRequest.amount(), "ok");
        }
        LOGGER.error("Failed to create order for cartId: {}", orderRequest.cartId());
        return new OrderResponse(orderRequest.cartId(), orderRequest.amount(), "ko");
    }
}
