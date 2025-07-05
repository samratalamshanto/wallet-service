package com.samratalam.ewallet_system.service;

import com.samratalam.ewallet_system.dto.WalletToBankTransferCallBack;
import com.samratalam.ewallet_system.dto.WalletToBankTransferRequest;

public interface WalletService {

    void transferWalletToBank(WalletToBankTransferRequest request);

    void transferWalletToBankCallBack(WalletToBankTransferCallBack request);
}
