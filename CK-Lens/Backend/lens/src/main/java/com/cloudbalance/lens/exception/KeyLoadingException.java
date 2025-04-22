package com.cloudbalance.lens.exception;

public class KeyLoadingException extends RuntimeException {
    public KeyLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyLoadingException(String message) {
        super(message);
    }
}
