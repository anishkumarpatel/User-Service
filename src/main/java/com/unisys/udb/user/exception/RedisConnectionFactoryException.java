package com.unisys.udb.user.exception;

public class RedisConnectionFactoryException extends RuntimeException {
    public RedisConnectionFactoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
