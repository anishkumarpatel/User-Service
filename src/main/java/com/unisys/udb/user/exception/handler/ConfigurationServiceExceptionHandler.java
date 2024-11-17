package com.unisys.udb.user.exception.handler;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.exception.ConfigurationServiceException;
import com.unisys.udb.user.exception.ConfigurationServiceUnavailableException;
import com.unisys.udb.user.exception.WebClientIntegrationException;
import com.unisys.udb.user.exception.response.UdbExceptionResponse;
import com.unisys.udb.utility.linkmessages.dto.DynamicMessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

import static com.unisys.udb.user.constants.UdbConstants.EXCEPTION;
import static com.unisys.udb.user.constants.UdbConstants.FAILURE;

@Slf4j
@RestControllerAdvice
public class ConfigurationServiceExceptionHandler {
    @ExceptionHandler({WebClientIntegrationException.class})
    public ResponseEntity<Object> handleConfigurationResourcesNotFound(WebClientIntegrationException ex) {
        log.error(UdbConstants.EXCEPTION_MESSAGE, ex.getMessage(), ex);

        UdbExceptionResponse udbExceptionResponse = UdbExceptionResponse.builder()
                .errorMessage(ex.getMessage())
                .errorCode(ex.getHttpStatusCode().value())
                .httpStatus(HttpStatus.valueOf(ex.getHttpStatusCode().value()))
                .build();

        return new ResponseEntity<>(udbExceptionResponse, HttpStatus.valueOf(ex.getHttpStatusCode().value()));
    }

    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(ConfigurationServiceUnavailableException.class)
    public ResponseEntity<DynamicMessageResponse> configurationServiceUnavailableException(
            ConfigurationServiceUnavailableException configurationServiceUnavailableException) {
        log.error("ConfigurationServiceUnavailableException" + EXCEPTION, ExceptionUtils.getStackTrace(
                configurationServiceUnavailableException));
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(buildErrorResponse(configurationServiceUnavailableException.getErrorCode(),
                        configurationServiceUnavailableException.getErrorMessage(),
                        configurationServiceUnavailableException.getParams()));
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ConfigurationServiceException.class)
    public ResponseEntity<DynamicMessageResponse> configurationServiceException(
            ConfigurationServiceException configurationServiceException) {
        log.error("ConfigurationServiceInternalServerException" + EXCEPTION, ExceptionUtils.getStackTrace(
                configurationServiceException));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(configurationServiceException.getErrorCode(),
                        configurationServiceException.getErrorMessage(), configurationServiceException.getParams()));
    }

    private DynamicMessageResponse buildErrorResponse(String errorCode, String statusMessage, List<String> params) {
        DynamicMessageResponse.Message message = new DynamicMessageResponse.Message(errorCode, params);
        List<DynamicMessageResponse.Message> messages = new ArrayList<>();
        messages.add(message);

        return new DynamicMessageResponse(FAILURE, statusMessage, messages);
    }
}
