package com.bank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
public class AppValidationController {

    /**
     * Page de validation de la transaction pour retourner vers le site de billetterie
     * @param transactionId
     * @param status
     * @param cartId
     * @param amount
     * @param model
     * @return
     */
    @GetMapping("/payments/3ds")
    public String validation(
            @RequestParam(name = "transactionId") String transactionId,
            @RequestParam(name = "status") String status,
            @RequestParam(name = "cartId") String cartId,
            @RequestParam(name = "amount") Float amount,
            Model model) {
        model.addAttribute("transactionId", transactionId);
        model.addAttribute("status", status);
        model.addAttribute("cartId", cartId);
        model.addAttribute("amount", amount);
        return "validation";
    }
}
