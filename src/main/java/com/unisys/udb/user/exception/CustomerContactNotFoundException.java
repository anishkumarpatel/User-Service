package com.unisys.udb.user.exception;

public class CustomerContactNotFoundException extends RuntimeException {
    public CustomerContactNotFoundException(String message) {
        super(message);
    }
}