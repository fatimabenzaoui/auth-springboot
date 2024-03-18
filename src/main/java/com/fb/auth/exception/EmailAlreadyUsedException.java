package com.fb.auth.exception;

/**
 * Exception levée lorsqu'une tentative est faite pour utiliser une adresse e-mail qui est déjà associée à un autre compte utilisateur
 */
public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
