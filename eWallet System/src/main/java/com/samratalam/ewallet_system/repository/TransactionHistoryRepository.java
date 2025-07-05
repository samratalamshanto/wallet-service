package com.samratalam.ewallet_system.repository;

import com.samratalam.ewallet_system.entity.TransactionHistory;
import org.springframework.data.repository.CrudRepository;

public interface TransactionHistoryRepository extends CrudRepository<TransactionHistory, Long> {
    TransactionHistory getReferenceByTransactionId(String transactionId);
}
