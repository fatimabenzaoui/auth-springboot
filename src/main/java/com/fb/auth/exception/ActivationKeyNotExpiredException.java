package com.fb.auth.exception;

public class ActivationKeyNotExpiredException extends RuntimeException {
    public ActivationKeyNotExpiredException(String message) {
        super(message);
    }
}
