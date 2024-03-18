package com.fb.auth.exception;

/**
 * Exception levée lorsqu'une adresse e-mail est considérée comme invalide
 */
public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
