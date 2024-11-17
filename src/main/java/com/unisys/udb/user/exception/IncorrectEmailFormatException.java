package com.unisys.udb.user.exception;

public class IncorrectEmailFormatException extends RuntimeException {
    public IncorrectEmailFormatException(String email) {

        super("Incorrect email format: " + email);

    }
}

