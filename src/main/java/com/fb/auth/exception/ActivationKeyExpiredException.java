package com.fb.auth.exception;

import java.io.Serial;

public class ActivationKeyExpiredException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public ActivationKeyExpiredException() {
        super("*** ACTIVATION KEY EXPIRED ***");
    }
}
