package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.Payment;
import com.billetterie.payment.domain.Transaction;
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
