package com.unisys.udb.user.exception;

public class PinNotExistException extends RuntimeException {
    public PinNotExistException(String message) {
        super(message);
    }
}
