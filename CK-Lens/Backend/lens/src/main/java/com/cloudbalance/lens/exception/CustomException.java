package com.cloudbalance.lens.exception;

public class CustomException extends ApiException {

    public CustomException(String message) {
        super(message);
    }

    public static class UserNotFoundException extends ApiException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidCredentialsException extends ApiException {
        public InvalidCredentialsException() {
            super("Invalid username or password.");
        }
    }

    public static class TokenMissingException extends ApiException {
        public TokenMissingException() {
            super("Authorization token is missing or malformed.");
        }
    }

    public static class InvalidArgumentsException extends ApiException {
        public InvalidArgumentsException(String message) {
            super(message);
        }
    }

}
