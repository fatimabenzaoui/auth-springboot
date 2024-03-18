package com.fb.auth.exception;

import java.io.Serial;

public class InvalidEmailException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public InvalidEmailException() {
        super("*** INVALID EMAIL ***");
    }
}
