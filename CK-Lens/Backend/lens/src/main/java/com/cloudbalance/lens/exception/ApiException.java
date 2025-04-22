package com.cloudbalance.lens.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
