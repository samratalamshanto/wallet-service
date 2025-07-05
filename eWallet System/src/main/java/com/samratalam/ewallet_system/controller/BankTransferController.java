package com.samratalam.ewallet_system.controller;

import com.samratalam.ewallet_system.dto.BaseResponse;
import com.samratalam.ewallet_system.dto.WalletToBankTransferCallBack;
import com.samratalam.ewallet_system.dto.WalletToBankTransferRequest;
import com.samratalam.ewallet_system.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/bank/transfer")
@RequiredArgsConstructor
public class BankTransferController {
    private final WalletService walletService;

    @PostMapping
    public BaseResponse transferWalletToBank(@Valid @RequestBody WalletToBankTransferRequest request) {
        walletService.transferWalletToBank(request);
        return BaseResponse.successResponse(true);
    }


    @PostMapping("/call-back")
    public BaseResponse transferWalletToBankCallBack(@Valid @RequestBody WalletToBankTransferCallBack request) {
        walletService.transferWalletToBankCallBack(request);
        return BaseResponse.successResponse(true);
    }
}
