package com.fb.auth.exception;

/**
 * Exception levée lorsqu'une tentative est faite pour activer un compte utilisateur avec une clé d'activation introuvable
 */
public class ActivationKeyNotFoundException extends RuntimeException {
    public ActivationKeyNotFoundException(String message) {
        super(message);
    }
}
