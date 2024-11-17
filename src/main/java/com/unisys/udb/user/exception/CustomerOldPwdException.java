package com.unisys.udb.user.exception;

public class CustomerOldPwdException extends RuntimeException {
    public CustomerOldPwdException(String message) {
        super(message);
    }

    public CustomerOldPwdException(String message, Throwable cause) {
        super(message, cause);
    }
}

