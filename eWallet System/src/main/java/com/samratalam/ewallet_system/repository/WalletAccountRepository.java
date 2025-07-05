package com.samratalam.ewallet_system.repository;

import com.samratalam.ewallet_system.entity.WalletAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletAccountRepository extends JpaRepository<WalletAccount, Long> {
    WalletAccount getReferenceByWalletId(String walletId);
}
