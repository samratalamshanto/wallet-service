package com.samratalam.bankservice.utility;


import com.samratalam.bankservice.dto.WalletToBankTransferRequest;
import com.samratalam.bankservice.entity.BankAccount;
import com.samratalam.bankservice.exception.InsufficientAmountException;

import java.time.LocalDateTime;

public final class BankAccountUtil {

    public static synchronized String getReferenceId() {
        return "ref-" + LocalDateTime.now();
    }

    public static synchronized boolean isRequestedAmountValid(WalletToBankTransferRequest request, WalletAccount walletAccount) {
        Double requestedAmount = request.getAmount();

        if (!walletAccount.getCurrency().equals(request.getCurrency())) {
            requestedAmount = convertedAmount(request.getAmount(), walletAccount.getCurrency());
        }

        if (walletAccount.getBalance() < requestedAmount) {
            throw new InsufficientAmountException(request.getAmount());
        }
        return true;
    }

    public static Double convertedAmount(Double amount, String currency) {
        return 0.0; //todo: will implement the convertionRate for different currency
    }


    public static synchronized Double requestedAmountAfterConvertion(WalletToBankTransferRequest request, BankAccount bankAccount) {
        Double requestedAmount = request.getAmount();

        if (!bankAccount.getCurrency().equals(request.getCurrency())) {
            requestedAmount = convertedAmount(request.getAmount(), bankAccount.getCurrency());
        }

        return requestedAmount;
    }


}
