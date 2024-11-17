package com.unisys.udb.user.exception;

import com.unisys.udb.user.constants.UdbConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.unisys.udb.user.constants.UdbConstants.EXCEPTION;

@ControllerAdvice
@Slf4j
public class DigitalCustomerAlertExceptionHandler {
    private Map<String, Object> resp = new HashMap<>();

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {DigitalAlertNotFoundException.class})
    public ResponseEntity<Object> handleDigitalAlertNotFoundException(RuntimeException ex) {
        log.error(ex.getClass().getSimpleName() + EXCEPTION, ExceptionUtils.getStackTrace(ex));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.NOT_FOUND.value());
        resp.put(UdbConstants.MESSAGE, ex.getLocalizedMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = {DigitalAlertAlreadyExistsException.class})
    public ResponseEntity<Object> handleDigitalAlertAlreadyExistsException(RuntimeException ex) {
        log.error(ex.getClass().getSimpleName() + EXCEPTION, ExceptionUtils.getStackTrace(ex));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.CONFLICT.value());
        resp.put(UdbConstants.MESSAGE, ex.getLocalizedMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {InvalidDigitalAlertKeyException.class, InvalidDigitalCustomerProfileIdException.class})
    public ResponseEntity<Object> handleInvalidDigitalAlertKeyException(RuntimeException ex) {
        log.error(ex.getClass().getSimpleName() + EXCEPTION, ExceptionUtils.getStackTrace(ex));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        resp.put(UdbConstants.MESSAGE, ex.getLocalizedMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }
}
