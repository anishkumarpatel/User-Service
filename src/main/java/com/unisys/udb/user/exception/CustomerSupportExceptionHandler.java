package com.unisys.udb.user.exception;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.utils.dto.response.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

import static com.unisys.udb.user.constants.UdbConstants.EXCEPTION;

@ControllerAdvice
@Slf4j
public class CustomerSupportExceptionHandler {

    @ExceptionHandler(value = {CustomerNotFoundException.class})
    public ResponseEntity<UserAPIBaseResponse> handleCustomerNotFoundException(
            CustomerNotFoundException exception) {
        log.error("CustomerNotFoundException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .status(UdbConstants.NOT_FOUND)
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}