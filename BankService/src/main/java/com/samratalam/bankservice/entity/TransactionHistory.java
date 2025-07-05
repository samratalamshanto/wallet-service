package com.samratalam.bankservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bank_transaction_history")
@EqualsAndHashCode(callSuper = false)
public class TransactionHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transactionId;
    private String walletId;
    private String toAccount;
    private Double amount;
    private String currency;
    private String status;
    private String message;
    private String referenceId;
    private LocalDateTime processedAt;
}
