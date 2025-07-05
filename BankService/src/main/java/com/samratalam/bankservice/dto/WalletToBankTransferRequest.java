package com.samratalam.bankservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletToBankTransferRequest {
    @NotEmpty
    private String walletId;
    @NotEmpty
    private String toAccount;
    @NotEmpty
    private Double amount;
    @NotEmpty
    private String currency;
    @NotEmpty
    private String requestTransactionId;
}
