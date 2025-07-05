package com.samratalam.bankservice.repository;


import com.samratalam.bankservice.entity.ApiRequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiRequestHistoryRepo extends JpaRepository<ApiRequestHistory, Long> {
    List<ApiRequestHistory> findAllByRequestTransactionId(String transactionId);
}
