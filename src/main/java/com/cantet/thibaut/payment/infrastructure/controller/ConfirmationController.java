package com.cantet.thibaut.payment.infrastructure.controller;

import com.cantet.thibaut.payment.infrastructure.controller.dto.OrderDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ConfirmationController.PATH)
public class ConfirmationController {
    static final String PATH = "/confirmation";

    @GetMapping("/{id}")
    public OrderDto getOrder(@PathVariable("id") String id,
                             @RequestParam("amount") Float amount) {
        return new OrderDto(id, amount);
    }
}
