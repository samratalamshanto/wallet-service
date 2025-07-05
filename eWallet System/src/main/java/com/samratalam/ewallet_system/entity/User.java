package com.samratalam.ewallet_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String password;
    private String email;
    private String phone;
    private String address;

    @EqualsAndHashCode.Exclude
    @OneToOne(
            mappedBy = "walletUser",
            orphanRemoval = true
    )
    private WalletAccount walletAccount;
}
