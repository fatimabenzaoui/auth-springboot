package com.fb.auth.exception;

/**
 * Exception levée lorsqu'un mot de passe ne respecte pas la longueur requise
 */
public class InvalidLengthPasswordException extends RuntimeException {
    public InvalidLengthPasswordException(String message) {
        super(message);
    }
}
