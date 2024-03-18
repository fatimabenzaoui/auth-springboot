package com.fb.auth.exception;

import java.io.Serial;

public class UsernameAlreadyUsedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4028030767027870787L;
    public UsernameAlreadyUsedException() {
        super("*** USERNAME ALREADY USED ***");
    }
}
