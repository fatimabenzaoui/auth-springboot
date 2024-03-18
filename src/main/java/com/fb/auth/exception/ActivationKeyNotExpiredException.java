package com.fb.auth.exception;

/**
 * Exception levée lorsqu'une tentative est faite pour générer une nouvelle clé d'activation alors que la clé d'activation précédente est toujours valide et n'a pas encore expiré
 */
public class ActivationKeyNotExpiredException extends RuntimeException {
    public ActivationKeyNotExpiredException(String message) {
        super(message);
    }
}
