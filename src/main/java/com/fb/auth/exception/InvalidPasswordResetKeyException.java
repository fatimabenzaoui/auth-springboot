package com.fb.auth.exception;

/**
 * Exception levée lorsque la clé de réinitialisation du mot de passe est invalide ou expirée
 */
public class InvalidPasswordResetKeyException extends RuntimeException {
    public InvalidPasswordResetKeyException(String message) {
        super(message);
    }
}
