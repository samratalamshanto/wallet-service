package com.samratalam.bankservice.repository;

import com.samratalam.bankservice.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    BankAccount getReferenceByBankAccountNumber(String bankAccountNumber);
}
