package com.fb.auth.exception;

public class AccountAlreadyActivatedException extends RuntimeException {
    public AccountAlreadyActivatedException(String message) {
        super(message);
    }
}
