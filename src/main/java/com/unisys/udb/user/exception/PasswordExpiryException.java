package com.unisys.udb.user.exception;

public class PasswordExpiryException extends RuntimeException {
    public PasswordExpiryException(String message) {
        super(message);
    }
}
