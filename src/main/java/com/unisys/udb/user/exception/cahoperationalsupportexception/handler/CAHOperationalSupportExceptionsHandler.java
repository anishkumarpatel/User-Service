package com.unisys.udb.user.exception.cahoperationalsupportexception.handler;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.exception.cahoperationalsupportexception.DatabaseException;
import com.unisys.udb.user.exception.cahoperationalsupportexception.InvalidBroadCastMessageStatusException;
import com.unisys.udb.user.exception.cahoperationalsupportexception.BroadcastMessageNotFound;
import com.unisys.udb.user.exception.cahoperationalsupportexception.InvalidBroadcastMessageIdException;
import com.unisys.udb.user.exception.cahoperationalsupportexception.InvalidLocalCodeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.unisys.udb.user.constants.UdbConstants.EXCEPTION;

@Slf4j
@RestControllerAdvice
public class CAHOperationalSupportExceptionsHandler {
    private Map<String, Object> resp = new HashMap<>();

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {BroadcastMessageNotFound.class})
    public ResponseEntity<Object> handleNotFoundException(RuntimeException ex) {
        log.error(ex.getClass().getSimpleName() + EXCEPTION, ExceptionUtils.getStackTrace(ex));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.NOT_FOUND.value());
        resp.put(UdbConstants.MESSAGE, ex.getLocalizedMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {InvalidBroadcastMessageIdException.class, InvalidBroadCastMessageStatusException.class,
            InvalidLocalCodeException.class})
    public ResponseEntity<Object> handleBadRequestException(RuntimeException ex) {
        log.error(ex.getClass().getSimpleName() + EXCEPTION, ExceptionUtils.getStackTrace(ex));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        resp.put(UdbConstants.MESSAGE, ex.getLocalizedMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {DatabaseException.class})
    public ResponseEntity<Object> handleInternalServerException(RuntimeException ex) {
        log.error(ex.getClass().getSimpleName() + EXCEPTION, ExceptionUtils.getStackTrace(ex));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
        resp.put(UdbConstants.MESSAGE, ex.getLocalizedMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
}
