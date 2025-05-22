package com.billetterie.payment.use_case;

import com.billetterie.payment.domain.Bank;
import org.springframework.stereotype.Service;

@Service
public class CancelTransaction {
    private final Bank bank;

    public CancelTransaction(Bank bank) {
        this.bank = bank;
    }

    public boolean execute(CancelTransactionCommand  command) {
        return bank.cancel(command.transactionId(), command.amount());
    }
}
