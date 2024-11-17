package com.unisys.udb.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DeviceIdParamNotFoundException extends RuntimeException {

    public DeviceIdParamNotFoundException(String message) {
        super(message);
    }
}
