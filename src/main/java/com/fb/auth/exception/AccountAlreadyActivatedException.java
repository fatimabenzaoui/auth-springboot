package com.fb.auth.exception;

/**
 * Exception levée lorsqu'une tentative est faite pour activer un compte utilisateur déjà activé
 */
public class AccountAlreadyActivatedException extends RuntimeException {
    public AccountAlreadyActivatedException(String message) {
        super(message);
    }
}
