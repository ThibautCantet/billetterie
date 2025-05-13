package com.backofficebilletterie.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest) {
        if (orderRequest.cartId().contains("200")) {
            return new OrderResponse(orderRequest.cartId(), orderRequest.amount(), "ok");
        }
        return new OrderResponse(orderRequest.cartId(), orderRequest.amount(), "ko");
    }
}
