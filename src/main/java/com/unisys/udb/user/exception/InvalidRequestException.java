package com.unisys.udb.user.exception;

import lombok.Getter;

@Getter
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String msg) {
        super("Invalid  request : " + msg);
    }

    public InvalidRequestException(String message, String username) {
        super(message + ": " + username);
    }


}
