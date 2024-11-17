package com.unisys.udb.user.exception.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class UdbExceptionResponse {
    private String errorMessage;
    private int errorCode;
    private HttpStatus httpStatus;
}
