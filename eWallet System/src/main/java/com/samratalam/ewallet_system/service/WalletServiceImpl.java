package com.samratalam.ewallet_system.service;

import com.samratalam.ewallet_system.dto.WalletToBankTransferCallBack;
import com.samratalam.ewallet_system.dto.WalletToBankTransferRequest;
import com.samratalam.ewallet_system.entity.ApiRequestHistory;
import com.samratalam.ewallet_system.entity.TransactionHistory;
import com.samratalam.ewallet_system.entity.WalletAccount;
import com.samratalam.ewallet_system.enums.CommonStatus;
import com.samratalam.ewallet_system.exception.AlreadyProcessedTransaction;
import com.samratalam.ewallet_system.exception.InvalidLockException;
import com.samratalam.ewallet_system.redis.RedisLockManager;
import com.samratalam.ewallet_system.repository.ApiRequestHistoryRepo;
import com.samratalam.ewallet_system.repository.TransactionHistoryRepository;
import com.samratalam.ewallet_system.repository.WalletAccountRepository;
import com.samratalam.ewallet_system.utility.JsonUtil;
import com.samratalam.ewallet_system.utility.WalletUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.samratalam.ewallet_system.utility.WalletUtil.isRequestedAmountValid;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletAccountRepository repository;
    private final ApiRequestHistoryRepo apiRequestHistoryRepo;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final RedisLockManager redisLockManager;

    @Transactional
    @Override
    public void transferWalletToBank(WalletToBankTransferRequest request) {
        String lockValue = UUID.randomUUID().toString();
        String lockKeyName = WalletUtil.getWalletLockName(request.getWalletId());
        try {
            acquireDistributedLock(lockKeyName, lockValue);
            WalletAccount walletAccount = validateRequest(request);
            saveTransactionHistory(request);
            CompletableFuture.runAsync(() -> bankServiceApiCall(request));
            updateWallet(walletAccount, request);
            saveRequestHistory(request);
        } finally {
            releaseDistributedLock(lockKeyName, lockValue);
        }
    }

    private void releaseDistributedLock(String lockKeyName, String lockValue) {
        redisLockManager.releaseLock(lockKeyName, lockValue);
    }

    @Retryable(
            retryFor = InvalidLockException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    private void acquireDistributedLock(String lockKeyName, String lockValue) {
        var returnedLock = redisLockManager.acquireLock(lockKeyName, lockValue);
        if (returnedLock == null || !lockValue.equals(returnedLock)) {
            throw new InvalidLockException("Cannot acquired the lock");
        }
    }

    private void updateWallet(WalletAccount walletAccount, WalletToBankTransferRequest request) {
        Double requestAmount = WalletUtil.requestedAmountAfterConvertion(request, walletAccount);
        walletAccount.setBalance(walletAccount.getBalance() - requestAmount);
        repository.save(walletAccount);
    }

    @Override
    public void transferWalletToBankCallBack(WalletToBankTransferCallBack request) {
        TransactionHistory history = validateCallBackRequest(request);
        updateCallBacks(request, history);
    }

    private void updateCallBacks(WalletToBankTransferCallBack request, TransactionHistory history) {
        history.setMessage(request.getMessage());
        history.setReferenceId(request.getReferenceId());
        history.setStatus(request.getStatus());
        history.setProcessedAt(request.getProcessAt());
        transactionHistoryRepository.save(history);
    }

    @SneakyThrows
    private TransactionHistory validateCallBackRequest(WalletToBankTransferCallBack request) {
        TransactionHistory history = transactionHistoryRepository.getReferenceByTransactionId(request.getTransactionId());
        if (history == null) {
            throw new BadRequestException("No transaction found with transaction id " + request.getTransactionId());
        }
        return history;
    }

    private void saveTransactionHistory(WalletToBankTransferRequest request) {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setTransactionId(request.getRequestTransactionId());
        transactionHistory.setAmount(request.getAmount());
        transactionHistory.setCurrency(request.getCurrency());
        transactionHistory.setWalletId(request.getWalletId());
        transactionHistory.setToAccount(request.getToAccount());
        transactionHistory.setStatus(CommonStatus.PROCESSING.name());
        transactionHistoryRepository.save(transactionHistory);
    }

    private void saveRequestHistory(WalletToBankTransferRequest request) {
        ApiRequestHistory apiRequestHistory = new ApiRequestHistory();
        apiRequestHistory.setRequestTransactionId(request.getRequestTransactionId());
        apiRequestHistoryRepo.save(apiRequestHistory);
    }

    private WalletAccount validateRequest(WalletToBankTransferRequest request) {
        WalletAccount walletAccount = repository.getReferenceByWalletId(request.getWalletId());
        isRequestedAmountValid(request, walletAccount);

        var historyList = apiRequestHistoryRepo.findAllByRequestTransactionId(request.getRequestTransactionId());
        if (!historyList.isEmpty()) {
            throw new AlreadyProcessedTransaction(request.getRequestTransactionId());
        }
        return walletAccount;
    }

    @SneakyThrows
    private void bankServiceApiCall(WalletToBankTransferRequest requestBody) {
        HttpClient httpClient = HttpClient.newBuilder().build();
        String postUrl = "http://localhost:9090/api/v1/transfer";

        String postBody = JsonUtil.toJson(requestBody);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(postUrl))
                .header("Content-Type", "application/json")
                .header("TransactionId", requestBody.getRequestTransactionId())
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new BadRequestException(response.body());
        }
    }
}
