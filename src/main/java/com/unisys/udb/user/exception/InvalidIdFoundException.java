package com.unisys.udb.user.exception;

public class InvalidIdFoundException extends RuntimeException {
    public InvalidIdFoundException(Integer i) {
        super("Invalid Id, not found: " + i);

    }
}
