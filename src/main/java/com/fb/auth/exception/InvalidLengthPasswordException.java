package com.fb.auth.exception;

import java.io.Serial;

public class InvalidLengthPasswordException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -4797836925465502590L;
    public InvalidLengthPasswordException() {
        super("*** INVALID PASSWORD ***");
    }
}
