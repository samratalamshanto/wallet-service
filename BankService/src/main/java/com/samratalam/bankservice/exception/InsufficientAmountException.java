package com.samratalam.bankservice.exception;

public class InsufficientAmountException extends RuntimeException {
    public InsufficientAmountException() {
        super();
    }

    public InsufficientAmountException(String message) {
        super(message);
    }

    public InsufficientAmountException(Double amount) {
        super("Insufficient amount: " + amount);
    }
}
