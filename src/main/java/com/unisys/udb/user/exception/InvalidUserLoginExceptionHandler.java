package com.unisys.udb.user.exception;
import com.unisys.udb.utility.linkmessages.dto.DynamicMessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

import static com.unisys.udb.user.constants.UdbConstants.FAILURE;

@ControllerAdvice
@Slf4j
public class InvalidUserLoginExceptionHandler {

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<DynamicMessageResponse> handleInvalidUser(InvalidUserException invalidUserException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildErrorResponse(invalidUserException.getErrorCodes(), invalidUserException.getStatusMessage(),
                        invalidUserException.getParams()));
    }

    @ExceptionHandler(UserLockedException.class)
    public ResponseEntity<DynamicMessageResponse> handleUserLocked(UserLockedException userLockedException) {
        log.error("UserLockedException: {}", userLockedException.getMessage(), userLockedException);
        return ResponseEntity.status(HttpStatus.LOCKED)
                .body(buildErrorResponse(userLockedException.getErrorCode(), userLockedException.getErrorMessage(),
                        userLockedException.getParams()));
    }


    private DynamicMessageResponse buildErrorResponse(List<String> messageCodes,
                                                      String statusMessage,
                                                      List<String> params) {
        List<DynamicMessageResponse.Message> messages = messageCodes.stream()
                .map(code -> {
                    List<String> messageParams = new ArrayList<>(params); // Create a copy to ensure immutability
                    return new DynamicMessageResponse.Message(code, messageParams);
                })
                .toList(); // Use Stream.toList() instead of Stream.collect(Collectors.toList())

        return new DynamicMessageResponse(FAILURE, statusMessage, messages);
    }





}
