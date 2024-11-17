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
import java.util.concurrent.ExecutionException;

import static com.unisys.udb.user.constants.UdbConstants.EXCEPTION;
import static com.unisys.udb.user.constants.UdbConstants.FAILURE;

@ControllerAdvice
@Slf4j
public class UserDigitalAccessUpdateExceptionHandler {
    @ExceptionHandler(value = {UserStatusException.class})
    public ResponseEntity<UserAPIBaseResponse> handleUpdateUserAccountStatus(
            UserStatusException exception) {
        log.error("UserStatusException " + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.NOT_ACCEPTABLE)
                .status("Invalid Digital Access Status")
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }

    @ExceptionHandler(value = {ExecutionException.class, InterruptedException.class})
    public ResponseEntity<UserAPIBaseResponse> handleUpdateUserAccountStatus(
            Exception exception) {
        log.error("Exception " + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .status("Exception occurred while publishing record to kafka")
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    @ExceptionHandler(value = {InvalidUpdateField.class})
    public ResponseEntity<UserAPIBaseResponse> handleInvalidUpdateField(
            InvalidUpdateField exception) {
        log.error("InvalidUpdateField " + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .status(UdbConstants.BAD_REQUEST)
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = {InvalidActionException.class})
    public ResponseEntity<UserAPIBaseResponse> handleUpdateUserAccountStatus(
            InvalidActionException exception) {
        log.error("InvalidActionException " + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .status(UdbConstants.BAD_REQUEST)
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(PublishNotificationFailureException.class)
    public ResponseEntity<UserAPIBaseResponse> handlePublishNotificationFailureException(
            PublishNotificationFailureException exception) {
        log.error("PublishNotificationFailureException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .status("Exception from Notification Orchestrator service")
                .message(exception.getMessage())
                .timeStamp(CommonUtil.getCurrentDateInUTC())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    @ExceptionHandler(PasswordExpiryException.class)
    public ResponseEntity<UserAPIBaseResponse> handlePasswordExpiryException(
            PasswordExpiryException exception) {
        log.error("PasswordExpiryException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .status(UdbConstants.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .timeStamp(CommonUtil.getCurrentDateInUTC())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(DigitalPinStorageException.class)
    public ResponseEntity<UserAPIBaseResponse> handleDigitalPinStorageException(
            DigitalPinStorageException exception) {
        log.error("DigitalPinStorageException " + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .status(UdbConstants.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    @ExceptionHandler(NotificationRequestInputFieldException.class)
    public ResponseEntity<UserAPIBaseResponse> handleNotificationRequestInputFieldException(
            NotificationRequestInputFieldException exception) {
        log.error("NotificationRequestInputFieldException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .status("Notification request invalid")
                .message(exception.getMessage())
                .timeStamp(CommonUtil.getCurrentDateInUTC())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    @ExceptionHandler(NotificationFailure.class)
    public ResponseEntity<UserAPIBaseResponse> handleNotificationFailure(
            NotificationFailure exception) {
        log.error("NotificationFailure" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.ACCEPTED)
                .status("Exception occurred when publishing notification")
                .message(exception.getMessage())
                .timeStamp(CommonUtil.getCurrentDateInUTC())
                .build();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @ExceptionHandler(StatusUpdateReasonNotFoundException.class)
    public ResponseEntity<UserAPIBaseResponse> handleNotificationFailure(
            StatusUpdateReasonNotFoundException exception) {
        log.error("StatusUpdateReasonNotFoundException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .status(FAILURE)
                .message(exception.getMessage())
                .timeStamp(CommonUtil.getCurrentDateInUTC())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
