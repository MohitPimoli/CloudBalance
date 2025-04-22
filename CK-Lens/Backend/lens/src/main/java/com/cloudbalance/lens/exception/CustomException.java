package com.cloudbalance.lens.exception;

public class CustomException {

    public static class UserNotFoundException extends ApiException {
        public UserNotFoundException(String username) {
            super("User not found with username: " + username);
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
}
