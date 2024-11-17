package com.unisys.udb.user.exception;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;


@Data
@Getter
@RequiredArgsConstructor
public class InvalidUserException extends RuntimeException {
    private final List<String> errorCodes;
    private final HttpStatus httpStatus;
    private final String responseType;
    private final String statusMessage;
    @NonNull
    private final List<String> params;

    public InvalidUserException(String[] errorCodes, HttpStatus httpStatus,
                                String responseType, String statusMessage,
                                List<String> params) {
        this.errorCodes = Arrays.asList(errorCodes);
        this.httpStatus = httpStatus;
        this.responseType = responseType;
        this.statusMessage = statusMessage;
        this.params = params;
    }

}