package com.fb.auth.exception;

/**
 * Exception levée lorsque deux mots de passe ne correspondent pas
 */
public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String message) { super(message); }
}
