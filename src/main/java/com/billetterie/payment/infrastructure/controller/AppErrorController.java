package com.billetterie.payment.infrastructure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(AppErrorController.PATH)
public class AppErrorController {
    static final String PATH = "/panier-reserve-error";

    @GetMapping
    public String getCart(@RequestParam("cartId") String cartId,
                           @RequestParam("amount") Float amount,
                           Model model) {
        model.addAttribute("cartId", cartId);
        model.addAttribute("amount", amount);
        return "error";
    }
}
