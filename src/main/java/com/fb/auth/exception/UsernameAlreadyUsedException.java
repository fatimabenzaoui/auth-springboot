package com.fb.auth.exception;

/**
 * Exception levée lorsqu'une tentative est faite pour utiliser un surnom qui est déjà associé à un autre compte utilisateur
 */
public class UsernameAlreadyUsedException extends RuntimeException {
    public UsernameAlreadyUsedException(String message) {
        super(message);
    }
}
