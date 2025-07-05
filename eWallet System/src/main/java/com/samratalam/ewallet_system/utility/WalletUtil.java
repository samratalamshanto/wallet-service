package com.samratalam.ewallet_system.utility;

import com.samratalam.ewallet_system.dto.WalletToBankTransferRequest;
import com.samratalam.ewallet_system.entity.WalletAccount;
import com.samratalam.ewallet_system.exception.InsufficientAmountException;

public final class WalletUtil {

    public static synchronized String getWalletId(String userId) {
        return "wallet_" + String.format("%05d", userId);
    }

    public static synchronized String getWalletLockName(String walletId) {
        return "lock_key_" + walletId;
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


    public static synchronized Double requestedAmountAfterConvertion(WalletToBankTransferRequest request, WalletAccount walletAccount) {
        Double requestedAmount = request.getAmount();

        if (!walletAccount.getCurrency().equals(request.getCurrency())) {
            requestedAmount = convertedAmount(request.getAmount(), walletAccount.getCurrency());
        }

        if (walletAccount.getBalance() < requestedAmount) {
            throw new InsufficientAmountException(request.getAmount());
        }
        return requestedAmount;
    }

}
