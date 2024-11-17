package com.unisys.udb.user.exception;

public class InvalidPhoneNumberException extends RuntimeException {
    public InvalidPhoneNumberException(String phone) {
        super("Invalid number found: " + phone);

    }
}
