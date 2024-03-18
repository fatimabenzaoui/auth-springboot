package com.fb.auth.exception;

import java.io.Serial;

public class ActivationKeyNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public ActivationKeyNotFoundException() {
        super("*** UNKNOWN ACTIVATION KEY ***");
    }
}
