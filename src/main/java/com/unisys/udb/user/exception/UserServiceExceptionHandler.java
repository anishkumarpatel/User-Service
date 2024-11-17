package com.unisys.udb.user.exception;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.utils.dto.response.CommonUtil;
import com.unisys.udb.user.utils.dto.response.UdbExceptionModel;
import com.unisys.udb.utility.linkmessages.dto.DynamicMessageResponse;
import com.unisys.udb.utility.linkmessages.handler.DynamicMessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.unisys.udb.user.constants.UdbConstants.EXCEPTION;


@ControllerAdvice
@Slf4j
public class UserServiceExceptionHandler {

    private Map<String, Object> resp = new HashMap<>();

    @ExceptionHandler(value = {UserIdNotFoundException.class})
    public ResponseEntity<Object> handleUserIdNotFoundException(UserIdNotFoundException invalid) {
        log.error("UserIdNotFoundException" + EXCEPTION, ExceptionUtils.getStackTrace(invalid));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.NOT_FOUND.value());
        resp.put(UdbConstants.MESSAGE, invalid.getLocalizedMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }


    @ExceptionHandler(value = {DigitalPasswordStorageException.class})
    public ResponseEntity<Object> handleDigitalPasswordStorageException(DigitalPasswordStorageException exception) {
        log.error("DigitalPasswordStorageException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
        resp.put(UdbConstants.MESSAGE, exception.getLocalizedMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }


    @ExceptionHandler(value = {DigitalCustomerProfileIdNotFoundException.class})
    public ResponseEntity<DynamicMessageResponse> handleDigitalProfileIdNotFound(
            DigitalCustomerProfileIdNotFoundException exception) {
        log.error("DigitalCustomerProfileIdNotFoundException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        DynamicMessageBuilder dynamicMessageBuilder = new DynamicMessageBuilder();
        DynamicMessageResponse response = dynamicMessageBuilder.buildErrorResponse(exception.getErrorCode(),
                exception.getErrorMessage(), exception.getParams(), exception.getResponseType());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = {DigitalCustomerStatusTypeRefException.class})
    public ResponseEntity<DynamicMessageResponse> handleDigitalCustomerStatusTypeNotFound(
            DigitalCustomerStatusTypeRefException exception) {
        log.error("DigitalCustomerStatusTypeNotFoundException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        DynamicMessageBuilder dynamicMessageBuilder = new DynamicMessageBuilder();
        DynamicMessageResponse response = dynamicMessageBuilder.buildErrorResponse(exception.getErrorCode(),
                exception.getErrorMessage(), exception.getParams(), exception.getResponseType());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DigitalCustomerShortcutRequestNotFound.class)
    public ResponseEntity<DynamicMessageResponse> shortcutRequestNotFound(
            DigitalCustomerShortcutRequestNotFound exception) {
        log.error("DigitalCustomerShortcutRequestNotFound" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        DynamicMessageBuilder dynamicMessageBuilder = new DynamicMessageBuilder();
        DynamicMessageResponse response = dynamicMessageBuilder.buildErrorResponse(exception.getErrorCode(),
                exception.getErrorMessage(), exception.getParams(), exception.getResponseType());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }

    @ExceptionHandler(value = {DigitalCustomerProfileAlreadyExistsException.class})
    public ResponseEntity<UserAPIBaseResponse> handleDigitalCustomerProfileIdAlreadyExistsException(
            DigitalCustomerProfileAlreadyExistsException exception) {
        log.error("DigitalCustomerProfileAlreadyExistsException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));

        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .status("Already Exists")
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(value = {CoreCustomerProfileAlreadyExistsException.class})
    public ResponseEntity<DynamicMessageResponse> handleCoreCustomerProfileIdAlreadyExistsException(
            CoreCustomerProfileAlreadyExistsException exception) {

        log.error("CoreCustomerProfileAlreadyExistsException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));

        DynamicMessageBuilder dynamicMessageBuilder = new DynamicMessageBuilder();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                dynamicMessageBuilder.buildErrorResponse(exception.getErrorCode(),
                        exception.getErrorMessage(), exception.getParams(), exception.getResponseType()));
    }

    @ExceptionHandler(value = {DuplicationKeyException.class})
    public ResponseEntity<DynamicMessageResponse> handleDuplicationKeyException(
            DuplicationKeyException exception) {

        log.error("DuplicationKeyException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));

        DynamicMessageBuilder dynamicMessageBuilder = new DynamicMessageBuilder();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                dynamicMessageBuilder.buildErrorResponse(exception.getErrorCode(),
                        exception.getErrorMessage(), exception.getParams(), exception.getResponseType()));
    }

    @ExceptionHandler(value = {CoreCustomerProfileEmptyException.class})
    public ResponseEntity<DynamicMessageResponse> handleCoreCustomerProfileEmptyException(
            CoreCustomerProfileEmptyException exception) {

        log.error("Core Customer Profile id Missing" + EXCEPTION, ExceptionUtils.getStackTrace(exception));

        DynamicMessageBuilder dynamicMessageBuilder = new DynamicMessageBuilder();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                dynamicMessageBuilder.buildErrorResponse(exception.getErrorCode(),
                        exception.getErrorMessage(), exception.getParams(), exception.getResponseType()));
    }

    @ExceptionHandler(value = {UserNameNotFoundException.class})
    public ResponseEntity<Object> handleUserNameNotFoundException(
            UserNameNotFoundException invalid) {
        log.error("UserNameNotFoundException" + EXCEPTION, ExceptionUtils.getStackTrace(invalid));

        resp.put(UdbConstants.STATUS_CODE, HttpStatus.NOT_FOUND.value());
        resp.put(UdbConstants.MESSAGE, invalid.getLocalizedMessage() + " is Not Found");
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }

    @ExceptionHandler(value = {CoreCustomerProfileIdNotFoundException.class})
    public ResponseEntity<UserAPIBaseResponse> handleCoreCustomerProfileIdNotFoundException(
            CoreCustomerProfileIdNotFoundException exception) {
        log.error("CoreCustomerProfileIdNotFoundException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));

        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .status(UdbConstants.NOT_FOUND)
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = {DigitalCustomerShortcutUpdateException.class})
    public ResponseEntity<UserAPIBaseResponse> digitalCustomerShortcutUpdateException(
            DigitalCustomerShortcutUpdateException exception) {
        log.error("DigitalCustomerShortcutUpdateException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .status("Error updating digital customer shortcut: " + exception.getMessage())
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(value = {DigitalCustomerDeviceNotFoundException.class})
    public ResponseEntity<UserAPIBaseResponse> handleDigitalCustomerDeviceNotFound(
            DigitalCustomerDeviceNotFoundException exception) {
        log.error("DigitalCustomerDeviceNotFoundException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .status(UdbConstants.NOT_FOUND)
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = {DigitalCustomerDeviceIdNotFoundException.class})
    public ResponseEntity<UdbExceptionModel> handleInvalidDigitalCustomerDeviceId(
            DigitalCustomerDeviceIdNotFoundException exception) {
        Date timestamp = CommonUtil.getCurrentDateInUTC();
        log.error("DigitalCustomerDeviceIdNotFoundException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        UdbExceptionModel response = UdbExceptionModel.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .errorMessage("DigitalCustomerDeviceId not Found")
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = {DigitalCustomerSessionHistoryNotFoundException.class})
    public ResponseEntity<UserAPIBaseResponse> handleDigitalCustomerSessionHistoryNotFound(
            DigitalCustomerSessionHistoryNotFoundException exception) {
        log.error("DigitalCustomerSessionHistoryNotFoundException" + EXCEPTION,
                ExceptionUtils.getStackTrace(exception));
        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .status(UdbConstants.NOT_FOUND)
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserPublicKeyAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> handleUserPublicKeyAlreadyExist(
            UserPublicKeyAlreadyExistException exception) {
        log.error("handleUserPublicKeyAlreadyExist " + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.CONFLICT.value());
        resp.put(UdbConstants.MESSAGE, exception.getMessage());
        resp.put(UdbConstants.TIMESTAMP, UdbConstants.getCreateOrUpdateDate());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<UserAPIBaseResponse> handleDataAccessException(DataAccessException exception) {
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .status(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .message(exception.getMessage())
                .timeStamp(CommonUtil.getCurrentDateInUTC())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UserPublicKeyNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserPublicKeyNotFoundException(
            UserPublicKeyNotFoundException exception) {
        log.error("handleUserPublicKeyNotFoundException " + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.NOT_FOUND.value());
        resp.put(UdbConstants.MESSAGE, UdbConstants.PUBLIC_KEY_NOT_FOUND_FOR_THE_GIVEN_DEVICE_UUID);
        resp.put(UdbConstants.TIMESTAMP, UdbConstants.getCreateOrUpdateDate());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }

    @ExceptionHandler(DatabaseOperationsException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseOperationsException(
            DatabaseOperationsException exception) {
        log.error("handleDatabaseOperationsException " + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
        resp.put(UdbConstants.MESSAGE, UdbConstants.ERROR_RETRIEVING_PUBLIC_KEY_FOR_DEVICE_UDID);
        resp.put(UdbConstants.TIMESTAMP, UdbConstants.getCreateOrUpdateDate());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);

    }

    @ExceptionHandler(value = {DigitalDeviceUdidNotFoundException.class})
    public ResponseEntity<UserAPIBaseResponse> handleDigitalDeviceUdidNotFound(
            DigitalDeviceUdidNotFoundException exception) {
        log.error("DigitalDeviceUdidNotFoundException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .status(UdbConstants.NOT_FOUND)
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = {CustomerContactNotFoundException.class})
    public ResponseEntity<UserAPIBaseResponse> handleCustomerContactNotFound(
            CustomerContactNotFoundException exception) {
        log.error("CustomerContactNotFoundException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .status(UdbConstants.NOT_FOUND)
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClientResponseException(WebClientResponseException ex) {
        log.error(EXCEPTION, ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.getResponseBodyAsString());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {MarketingPreferenceException.class, NotificationPreferenceException.class})
    public ResponseEntity<Object> handleInternalServerException(RuntimeException ex) {
        log.error(ex.getClass().getSimpleName() + EXCEPTION, ExceptionUtils.getStackTrace(ex));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
        resp.put(UdbConstants.MESSAGE, ex.getLocalizedMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }


    @ExceptionHandler(value = {RuleEngineIntegrationException.class})
    public ResponseEntity<UserAPIBaseResponse> handleRuleEngineIntegrationException(
            RuleEngineIntegrationException exception) {
        log.error("Unable to connect to Rule Engine Service " + EXCEPTION,
                ExceptionUtils.getStackTrace(exception));
        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .status(UdbConstants.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(value = {PinNotExistException.class})
    public ResponseEntity<UserAPIBaseResponse> handlePinNotExistException(
            PinNotExistException exception) {

        log.error("Pin not setup" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .status(UdbConstants.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}