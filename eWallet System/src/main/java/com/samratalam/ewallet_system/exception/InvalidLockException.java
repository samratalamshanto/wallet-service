package com.samratalam.ewallet_system.exception;

public class InvalidLockException extends RuntimeException {
    public InvalidLockException() {
        super();
    }

    public InvalidLockException(String message) {
        super(message);
    }
}
