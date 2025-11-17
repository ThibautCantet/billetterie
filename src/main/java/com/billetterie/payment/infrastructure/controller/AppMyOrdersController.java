package com.billetterie.payment.infrastructure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(AppMyOrdersController.PATH)
public class AppMyOrdersController {
    static final String PATH = "/my-orders";

    @GetMapping
    public String getOrder(@RequestParam("id") String id,
                           @RequestParam("amount") Float amount,
                           Model model) {
        model.addAttribute("id", id);
        model.addAttribute("amount", amount);
        return "my-orders";
    }
}
