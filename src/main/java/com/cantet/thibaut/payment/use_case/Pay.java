package com.cantet.thibaut.payment.use_case;

import com.cantet.thibaut.payment.domain.Bank;
import com.cantet.thibaut.payment.domain.Payment;
import com.cantet.thibaut.payment.domain.Transaction;
import org.springframework.stereotype.Service;

@Service
public class Pay {
    private final Bank bank;

    public Pay(Bank bank) {
        this.bank = bank;
    }

    public Transaction execute(PayCommand command) {
        Payment payment = new Payment(command.cardNumber(), command.expirationDate(), command.cypher(), command.cartId(), command.amount());
        return bank.pay(payment);
    }
}
