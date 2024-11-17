package com.unisys.udb.user.exception;

public class DuplicateEmailFoundException extends RuntimeException {
    public DuplicateEmailFoundException(String email) {

        super("Duplicate email found: " + email);

    }
}

