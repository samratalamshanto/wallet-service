package com.samratalam.bankservice.repository;


import com.samratalam.bankservice.entity.TransactionHistory;
import org.springframework.data.repository.CrudRepository;

public interface TransactionHistoryRepository extends CrudRepository<TransactionHistory, Long> {
    TransactionHistory getReferenceByTransactionId(String transactionId);
}
