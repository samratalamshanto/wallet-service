package com.samratalam.bankservice.exception;

public class AlreadyProcessedTransaction extends RuntimeException {
    public AlreadyProcessedTransaction() {
        super();
    }

    public AlreadyProcessedTransaction(String transactionId) {
        super("Already Processed this transaction: " + transactionId);
    }
}
