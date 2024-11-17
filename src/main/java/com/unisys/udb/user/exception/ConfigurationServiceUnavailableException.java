package com.unisys.udb.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ConfigurationServiceUnavailableException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String responseType;
    private final String errorMessage;
    private final List<String> params;
}
