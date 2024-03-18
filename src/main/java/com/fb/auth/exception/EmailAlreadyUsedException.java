package com.fb.auth.exception;

import java.io.Serial;

public class EmailAlreadyUsedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -6278656429409224449L;
    public EmailAlreadyUsedException() {
        super("*** EMAIL ALREADY USED ***");
    }
}
