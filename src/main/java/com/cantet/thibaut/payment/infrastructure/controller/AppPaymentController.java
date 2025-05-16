package com.cantet.thibaut.payment.infrastructure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(AppPaymentController.PATH)
public class AppPaymentController {

    static final String PATH = "/";

    @GetMapping
    public String payment() {
        return "payment";
    }
}
