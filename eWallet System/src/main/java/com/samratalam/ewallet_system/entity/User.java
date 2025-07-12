package com.samratalam.ewallet_system.entity;

import com.samratalam.ewallet_system.custom.annotation.StrongPassword;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

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
    @StrongPassword
    private String password;
    private String email;
    private String phone;
    private String address;
    @Past
    private LocalDate birthday;

    @EqualsAndHashCode.Exclude
    @OneToOne(
            mappedBy = "walletUser",
            orphanRemoval = true
    )
    private WalletAccount walletAccount;
}
