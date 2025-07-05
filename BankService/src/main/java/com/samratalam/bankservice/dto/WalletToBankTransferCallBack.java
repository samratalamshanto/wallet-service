package com.samratalam.bankservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletToBankTransferCallBack {
    @NotEmpty
    private String transactionId;
    @NotEmpty
    private String status;
    @NotEmpty
    private String message;
    @NotEmpty
    private String referenceId;
    private LocalDateTime processAt;
}
