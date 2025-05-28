package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.Bank;
import com.billetterie.payment.domain.Transaction;
import org.springframework.stereotype.Service;

@Service
public class Pay {
    private final Bank bank;

    public Pay(Bank bank) {
        this.bank = bank;
    }

    public Transaction execute(PayCommand command) {
        return null;
    }


}
