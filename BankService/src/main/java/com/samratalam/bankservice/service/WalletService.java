package com.samratalam.bankservice.service;


import com.samratalam.bankservice.dto.WalletToBankTransferCallBack;
import com.samratalam.bankservice.dto.WalletToBankTransferRequest;

public interface WalletService {

    void transferWalletToBank(WalletToBankTransferRequest request);

    void transferWalletToBankCallBack(WalletToBankTransferCallBack request);
}
