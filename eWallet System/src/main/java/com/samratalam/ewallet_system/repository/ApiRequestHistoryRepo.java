package com.samratalam.ewallet_system.repository;

import com.samratalam.ewallet_system.entity.ApiRequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiRequestHistoryRepo extends JpaRepository<ApiRequestHistory, Long> {
    List<ApiRequestHistory> findAllByRequestTransactionId(String transactionId);
}
