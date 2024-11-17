package com.unisys.udb.user.exception;

public class MissingRequiredRequestParamException extends RuntimeException {
    public MissingRequiredRequestParamException(String s) {
        super("Request Parameter Missing : " + s);
    }
}
