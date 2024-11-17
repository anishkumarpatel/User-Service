package com.unisys.udb.user.exception;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.response.MissingFields;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.utils.dto.response.CommonUtil;
import com.unisys.udb.user.utils.dto.response.NotificationPreferenceResponse;
import com.unisys.udb.user.utils.dto.response.UdbExceptionModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static com.unisys.udb.user.constants.UdbConstants.EXCEPTION;

@ControllerAdvice
@Slf4j
public class UserServiceBadRequestExceptionHandler {
    private Map<String, Object> resp = new HashMap<>();

    @ExceptionHandler(value = {InvalidRequestException.class})
    public ResponseEntity<Object> handleInvalidRequestException(InvalidRequestException ex) {
        log.error(EXCEPTION, ExceptionUtils.getStackTrace(ex));

        resp.put(UdbConstants.STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        resp.put(UdbConstants.MESSAGE, ex.getLocalizedMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(MissingRequiredRequestParamException.class)
    public ResponseEntity<Map<String, Object>> handleMissingRequiredRequestParamException(
            MissingRequiredRequestParamException exception) {
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        resp.put(UdbConstants.MESSAGE, exception.getMessage());
        resp.put(UdbConstants.TIMESTAMP, UdbConstants.getCreateOrUpdateDate());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParams(MissingServletRequestParameterException exception) {
        log.error("MissingServletRequestParameterException: {}", exception.getMessage(), exception);
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        resp.put(UdbConstants.MESSAGE, UdbConstants.MISSING_REQUEST_PARAMETER);
        resp.put(UdbConstants.TIMESTAMP, UdbConstants.getCreateOrUpdateDate());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<List<MissingFields>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<MissingFields> missingFields = new ArrayList<>();

        bindingResult.getFieldErrors().forEach(fieldError -> {
            MissingFields missingField = new MissingFields();
            missingField.setFieldName(fieldError.getField());
            missingField.setMessage(fieldError.getDefaultMessage());
            missingFields.add(missingField);
        });



        return ResponseEntity.badRequest().body(missingFields);
    }

    @ExceptionHandler(value = {DigitalCustomerProfileIdNotNullException.class})
    public ResponseEntity<NotificationPreferenceResponse> handleDigitalCustomerProfileIdNotNullException(
            DigitalCustomerProfileIdNotNullException exception) {
        log.error("DigitalCustomerProfileIdNotNullException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        Date timestamp = CommonUtil.getCurrentDateInUTC();
        NotificationPreferenceResponse response = NotificationPreferenceResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .status("Bad Request")
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleInvalidInput(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException" + EXCEPTION, ExceptionUtils.getStackTrace(e));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        resp.put(UdbConstants.MESSAGE, UdbConstants.INVALID_REQUEST_PAYLOAD);
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<Object> handleInvalidArgument(InvalidArgumentException e) {
        log.error("InvalidArgumentException" + EXCEPTION, ExceptionUtils.getStackTrace(e));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        resp.put(UdbConstants.MESSAGE, e.getMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<UserAPIBaseResponse> handleValidationException(IllegalArgumentException exception) {
        log.error("IllegalArgumentException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));

        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UserAPIBaseResponse apiResponse = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .status(UdbConstants.BAD_REQUEST)
                .message(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(IncorrectEmailFormatException.class)
    public ResponseEntity<Map<String, Object>> handleIncorrectEmailFormatException(
            IncorrectEmailFormatException exception) {
        log.error("IncorrectEmailFormatException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        resp.put(UdbConstants.MESSAGE, UdbConstants.INCORRECT_EMAIL_FORMAT);
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(value = {InvalidDataException.class})
    public ResponseEntity<Object> handleInvalidDataException(InvalidDataException exception) {
        log.error("InvalidDataException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        resp.put(UdbConstants.MESSAGE, exception.getMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }
    @ExceptionHandler(value = {PasswordHistoryRetrievalException.class})
    public ResponseEntity<Object> handlePasswordHistoryRetrievalException(PasswordHistoryRetrievalException exception) {
        log.error("PasswordHistoryRetrievalException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
        resp.put(UdbConstants.MESSAGE, exception.getMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
    @ExceptionHandler(value = {InvalidDigitalDeviceUdid.class})
    public ResponseEntity<UdbExceptionModel> handleInvalidDigitalDeviceUdid(InvalidDigitalDeviceUdid exception) {
        log.error("InvalidDigitalDeviceUdid" + EXCEPTION, ExceptionUtils.getStackTrace(exception));

        Date timestamp = CommonUtil.getCurrentDateInUTC();
        UdbExceptionModel response = UdbExceptionModel.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .errorMessage(exception.getMessage())
                .timeStamp(timestamp)
                .build();
        return ResponseEntity.badRequest().body(response);
    }
    @ExceptionHandler(UserDeviceNotLinkedException.class)
    public ResponseEntity<Map<String, Object>> handleUserDeviceNotLinkedExceptionException(
            UserDeviceNotLinkedException exception) {
        log.error("handleUserDeviceNotLinkedExceptionException " + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.NOT_FOUND.value());
        resp.put(UdbConstants.MESSAGE, exception.getMessage());
        resp.put(UdbConstants.TIMESTAMP, UdbConstants.getCreateOrUpdateDate());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }

    @ExceptionHandler(value = {MaximumDevicesRegisteredException.class})
    public ResponseEntity<Object> handleMaximumDevicesRegisterException(MaximumDevicesRegisteredException invalid) {
        log.error("MaximumDevicesRegisteredException" + EXCEPTION, ExceptionUtils.getStackTrace(invalid));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.FORBIDDEN.value());
        resp.put(UdbConstants.MESSAGE, invalid.getLocalizedMessage());
        resp.put(UdbConstants.TIMESTAMP, ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime().toString());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resp);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception) {
        log.error("MethodArgumentTypeMismatchException" + EXCEPTION, ExceptionUtils.getStackTrace(exception));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        resp.put(UdbConstants.MESSAGE, UdbConstants.INCORRECT_UUID_FORMAT);
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(value = {UserNameAlreadyExistException.class})
    public ResponseEntity<Object> handleUserNameAlreadyExistException(UserNameAlreadyExistException invalid) {
        log.error("UserNameAlreadyExistException" + EXCEPTION, ExceptionUtils.getStackTrace(invalid));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        resp.put(UdbConstants.MESSAGE, invalid.getLocalizedMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(value = {DuplicateEmailFoundException.class})
    public ResponseEntity<Object> handleDuplicateEmailFoundException(DuplicateEmailFoundException invalid) {
        log.error("DuplicateEmailFoundException" + EXCEPTION, ExceptionUtils.getStackTrace(invalid));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        resp.put(UdbConstants.MESSAGE, invalid.getLocalizedMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(value = {PinNotExistException.class})
    public ResponseEntity<Object> handlePinNotExistException(PinNotExistException invalid) {
        log.error("PinNotExistException" + EXCEPTION, ExceptionUtils.getStackTrace(invalid));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        resp.put(UdbConstants.MESSAGE, invalid.getLocalizedMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }
    @ExceptionHandler(value = {MFAConfigNotFoundException.class})
    public ResponseEntity<Object> handleMFAConfigException(MFAConfigNotFoundException invalid) {
        log.error("MFA details not Existig in cache" + EXCEPTION, ExceptionUtils.getStackTrace(invalid));
        resp.put(UdbConstants.STATUS_CODE, HttpStatus.NOT_FOUND.value());
        resp.put(UdbConstants.MESSAGE, invalid.getLocalizedMessage());
        resp.put(UdbConstants.TIMESTAMP, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }
}
