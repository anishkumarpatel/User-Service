package com.unisys.udb.user.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;
@RequiredArgsConstructor
public class RegistrationNotificationNotPublished extends RuntimeException {

    private final List<String> errorCode;
    private final HttpStatus httpStatus;
    private final String responseType;
    private final String errorMessage;
    private final List<String> params;
}
