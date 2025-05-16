package com.cantet.thibaut.payment.infrastructure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(AppConfirmationController.PATH)
public class AppConfirmationController {
    static final String PATH = "/confirmation";

    @GetMapping("/{id}")
    public String getOrder(@PathVariable("id") String id,
                           @RequestParam("amount") Float amount,
                           Model model) {
        model.addAttribute("id", id);
        model.addAttribute("amount", amount);
        return "confirmation";
    }
}
