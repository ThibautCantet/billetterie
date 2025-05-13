package com.cantet.thibaut.payment.infrastructure.controller;

import com.cantet.thibaut.payment.infrastructure.controller.dto.CartDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CartController.PATH)
public class CartController {
    static final String PATH = "/cart";

    @GetMapping
    public CartDto getCart(@RequestParam("cartId") String cartId,
                           @RequestParam("amount") Float amount,
                           @RequestParam(value = "error", required = false) Boolean error) {
        return new CartDto(cartId, amount, error);
    }
}
