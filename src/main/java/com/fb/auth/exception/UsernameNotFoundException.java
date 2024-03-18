package com.fb.auth.exception;

/**
 * Exception levée lorsqu'aucun utilisateur n'est trouvé avec un surnom donné
 */
public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(String message) {
        super(message);
    }
}
