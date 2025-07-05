package com.samratalam.bankservice.controller;


import com.samratalam.bankservice.dto.BaseResponse;
import com.samratalam.bankservice.dto.WalletToBankTransferCallBack;
import com.samratalam.bankservice.dto.WalletToBankTransferRequest;
import com.samratalam.bankservice.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@Slf4j
@RequestMapping("/api/v1/bank/transfer")
@RequiredArgsConstructor
public class BankTransferController {
    private final WalletService walletService;

    @PostMapping
    public BaseResponse transferWalletToBank(@Valid @RequestBody WalletToBankTransferRequest request) {
        CompletableFuture.runAsync(() -> walletService.transferWalletToBank(request));
        return BaseResponse.successResponse(true);
    }
}
