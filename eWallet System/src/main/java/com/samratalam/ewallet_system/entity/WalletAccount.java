package com.samratalam.ewallet_system.entity;

import com.samratalam.ewallet_system.enums.WalletStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallet_accounts")
@EqualsAndHashCode(callSuper = false)
public class WalletAccount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String walletId;
    @EqualsAndHashCode.Exclude
    @OneToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User walletUser;
    private Double balance;
    private String currency;
    @Enumerated(EnumType.STRING)
    private WalletStatus status;
}
