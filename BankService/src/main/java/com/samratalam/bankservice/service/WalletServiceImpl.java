package com.samratalam.bankservice.service;

import com.samratalam.bankservice.dto.WalletToBankTransferCallBack;
import com.samratalam.bankservice.dto.WalletToBankTransferRequest;
import com.samratalam.bankservice.entity.ApiRequestHistory;
import com.samratalam.bankservice.entity.BankAccount;
import com.samratalam.bankservice.entity.TransactionHistory;
import com.samratalam.bankservice.enums.CommonStatus;
import com.samratalam.bankservice.exception.AlreadyProcessedTransaction;
import com.samratalam.bankservice.repository.ApiRequestHistoryRepo;
import com.samratalam.bankservice.repository.BankAccountRepository;
import com.samratalam.bankservice.repository.TransactionHistoryRepository;
import com.samratalam.bankservice.utility.BankAccountUtil;
import com.samratalam.bankservice.utility.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static com.samratalam.bankservice.utility.BankAccountUtil.getReferenceId;


@Service
@Slf4j
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final ApiRequestHistoryRepo apiRequestHistoryRepo;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final BankAccountRepository bankAccountRepository;

    @Transactional
    @Override
    public void transferWalletToBank(WalletToBankTransferRequest request) {
        TransactionHistory transactionHistory = saveTransactionHistory(request);
        BankAccount bankAccount = validateRequest(request, transactionHistory);
        updateBankAccount(bankAccount, request);
        transactionHistory = updateTransactionHistory(CommonStatus.SUCCESS, transactionHistory);
        TransactionHistory finalTransactionHistory = transactionHistory;
        CompletableFuture.runAsync(() -> successCallBack(finalTransactionHistory));
        saveRequestHistory(request);
    }


    private void updateBankAccount(BankAccount bankAccount, WalletToBankTransferRequest request) {
        Double requestAmount = BankAccountUtil.requestedAmountAfterConvertion(request, bankAccount);
        Double curAmount = ObjectUtils.isEmpty(bankAccount.getBalance()) ? 0.0 : bankAccount.getBalance();
        bankAccount.setBalance(curAmount + requestAmount);
        bankAccountRepository.save(bankAccount);
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

    private TransactionHistory saveTransactionHistory(WalletToBankTransferRequest request) {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setTransactionId(request.getRequestTransactionId());
        transactionHistory.setAmount(request.getAmount());
        transactionHistory.setCurrency(request.getCurrency());
        transactionHistory.setWalletId(request.getWalletId());
        transactionHistory.setToAccount(request.getToAccount());
        transactionHistory.setStatus(CommonStatus.PROCESSING.name());
        transactionHistory.setReferenceId(getReferenceId());
        transactionHistory.setProcessedAt(LocalDateTime.now());
        transactionHistory.setMessage(CommonStatus.PROCESSING.name());
        transactionHistory = transactionHistoryRepository.save(transactionHistory);
        return transactionHistory;
    }

    private void saveRequestHistory(WalletToBankTransferRequest request) {
        ApiRequestHistory apiRequestHistory = new ApiRequestHistory();
        apiRequestHistory.setRequestTransactionId(request.getRequestTransactionId());
        apiRequestHistoryRepo.save(apiRequestHistory);
    }

    @SneakyThrows
    private BankAccount validateRequest(WalletToBankTransferRequest request, TransactionHistory transactionHistory) {
        BankAccount bankAccount = bankAccountRepository.getReferenceByBankAccountNumber(request.getToAccount());
        if (bankAccount == null) {
            transactionHistory = updateTransactionHistory(CommonStatus.FAILED, transactionHistory);
            failedCallBacks(transactionHistory);
            throw new BadRequestException("Bank account not found");
        }

        var historyList = apiRequestHistoryRepo.findAllByRequestTransactionId(request.getRequestTransactionId());
        if (!historyList.isEmpty()) {
            throw new AlreadyProcessedTransaction(request.getRequestTransactionId());
        }
        return bankAccount;
    }

    private TransactionHistory updateTransactionHistory(CommonStatus commonStatus, TransactionHistory transactionHistory) {
        transactionHistory.setStatus(commonStatus.name());
        transactionHistory.setProcessedAt(LocalDateTime.now());
        transactionHistory.setMessage(commonStatus.name());
        return transactionHistoryRepository.save(transactionHistory);
    }


    @SneakyThrows
    private void walletServiceCallBackApiCall(WalletToBankTransferCallBack requestBody) {
        HttpClient httpClient = HttpClient.newBuilder().build();
        String postUrl = "http://localhost:8080/api/v1/bank/transfer";

        String postBody = JsonUtil.toJson(requestBody);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(postUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new BadRequestException(response.body());
        }
    }

    private void successCallBack(TransactionHistory transactionHistory) {
        WalletToBankTransferCallBack callBack = mapToCallBackRequest(transactionHistory);
        callBack.setStatus(CommonStatus.SUCCESS.name());
        callBack.setMessage(transactionHistory.getMessage());

        walletServiceCallBackApiCall(callBack);
    }

    private void failedCallBacks(TransactionHistory transactionHistory) {
        WalletToBankTransferCallBack callBack = mapToCallBackRequest(transactionHistory);
        callBack.setStatus(CommonStatus.FAILED.name());
        callBack.setMessage(CommonStatus.FAILED.name());

        walletServiceCallBackApiCall(callBack);
    }

    private WalletToBankTransferCallBack mapToCallBackRequest(TransactionHistory transactionHistory) {
        WalletToBankTransferCallBack callBack = new WalletToBankTransferCallBack();
        callBack.setTransactionId(transactionHistory.getTransactionId());
        callBack.setProcessAt(transactionHistory.getProcessedAt());
        callBack.setReferenceId(transactionHistory.getReferenceId());
        return callBack;
    }
}
