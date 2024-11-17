package com.unisys.udb.user.exception;

import com.unisys.udb.user.exception.response.UdbExceptionResponse;
import org.springframework.http.HttpStatusCode;



public class WebClientIntegrationException extends RuntimeException {
    private final transient UdbExceptionResponse exceptionResponse;
    private final HttpStatusCode httpStatusCode;

    public WebClientIntegrationException(UdbExceptionResponse exceptionResponse, HttpStatusCode httpStatusCode) {
        super(exceptionResponse.getErrorMessage());
        this.exceptionResponse = exceptionResponse;
        this.httpStatusCode = httpStatusCode;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
