package com.cantet.thibaut.payment.infrastructure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(AppCartController.PATH)
public class AppCartController {
    static final String PATH = "/cart";

    @GetMapping
    public String getCart(@RequestParam("cartId") String cartId,
                           @RequestParam("amount") Float amount,
                           @RequestParam(value = "error", required = false) Boolean error,
                           Model model) {
        model.addAttribute("cartId", cartId);
        model.addAttribute("amount", amount);
        model.addAttribute("error", error);
        return "cart";
    }
}
