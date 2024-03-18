package com.fb.auth.exception;

/**
 * Exception levée lorsqu'une tentative est faite pour activer un compte utilisateur avec une clé d'activation expirée
 */
public class ActivationKeyExpiredException extends RuntimeException {
    public ActivationKeyExpiredException(String message) { super(message); }
}
