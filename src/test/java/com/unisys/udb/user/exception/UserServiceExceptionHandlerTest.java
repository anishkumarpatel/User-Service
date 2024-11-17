package com.unisys.udb.user.exception;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.PrintWriter;
import java.util.Map;

import static com.unisys.udb.user.constants.UdbConstants.INTERNAL_ERROR_CODE;
import static com.unisys.udb.user.constants.UdbConstants.THREE_CONSTANT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceExceptionHandlerTest {
    private final int badRequest = 400;
    private final int notFound = 404;
    private final int expectedSize = 3;




    @Test
    void testHandleDuplicateEmailFoundException() {
        UserServiceExceptionHandler userServiceExceptionHandler = new UserServiceExceptionHandler();
        UserServiceBadRequestExceptionHandler userServiceBadRequestExceptionHandler =
                new UserServiceBadRequestExceptionHandler();
        ResponseEntity<Object> actualHandleDuplicateEmailFoundExceptionResult = userServiceBadRequestExceptionHandler
                .handleDuplicateEmailFoundException(new DuplicateEmailFoundException("jane.doe@example.org"));
        assertEquals(expectedSize, ((Map<String, Object>) actualHandleDuplicateEmailFoundExceptionResult
                .getBody()).size());
        assertEquals(badRequest, actualHandleDuplicateEmailFoundExceptionResult.getStatusCodeValue());
        assertTrue(actualHandleDuplicateEmailFoundExceptionResult.hasBody());
        assertTrue(actualHandleDuplicateEmailFoundExceptionResult.getHeaders().isEmpty());
    }

    @Test
    void testHandleUserIdNotFoundException() {
        UserServiceExceptionHandler userServiceExceptionHandler = new UserServiceExceptionHandler();
        ResponseEntity<Object> actualHandleUserIdNotFoundExceptionResult = userServiceExceptionHandler
                .handleUserIdNotFoundException(new UserIdNotFoundException(1));
        assertEquals(expectedSize, ((Map<String, Object>) actualHandleUserIdNotFoundExceptionResult.getBody()).size());
        assertEquals(notFound, actualHandleUserIdNotFoundExceptionResult.getStatusCodeValue());
        assertTrue(actualHandleUserIdNotFoundExceptionResult.hasBody());
        assertTrue(actualHandleUserIdNotFoundExceptionResult.getHeaders().isEmpty());
    }

    @Test
    void testHandleUserNameAlreadyExistException() {
        UserServiceBadRequestExceptionHandler userServiceBadRequestExceptionHandler =
                new UserServiceBadRequestExceptionHandler();
        ResponseEntity<Object> actualHandleUserNameAlreadyExistExceptionResult = userServiceBadRequestExceptionHandler
                .handleUserNameAlreadyExistException(new UserNameAlreadyExistException("janedoe"));
        assertEquals(expectedSize, ((Map<String, Object>) actualHandleUserNameAlreadyExistExceptionResult
                .getBody()).size());
        assertEquals(badRequest, actualHandleUserNameAlreadyExistExceptionResult.getStatusCodeValue());
        assertTrue(actualHandleUserNameAlreadyExistExceptionResult.hasBody());
        assertTrue(actualHandleUserNameAlreadyExistExceptionResult.getHeaders().isEmpty());
    }

    @Test
    void testHandleMethodArgumentTypeMismatchException() {
        UserServiceExceptionHandler userServiceExceptionHandler = new UserServiceExceptionHandler();
        UserServiceBadRequestExceptionHandler userServiceBadRequestExceptionHandler =
                new UserServiceBadRequestExceptionHandler();
        Class<Object> requiredType = Object.class;
        ResponseEntity<Map<String, Object>> actualHandleMethodArgumentTypeMismatchExceptionResult =
                userServiceBadRequestExceptionHandler.handleMethodArgumentTypeMismatchException(
                        new MethodArgumentTypeMismatchException(
                                "Value", requiredType, "0123456789ABCDEF", null, new Throwable()));
        assertEquals(expectedSize, actualHandleMethodArgumentTypeMismatchExceptionResult.getBody().size());
        assertEquals(badRequest, actualHandleMethodArgumentTypeMismatchExceptionResult.getStatusCodeValue());
        assertTrue(actualHandleMethodArgumentTypeMismatchExceptionResult.hasBody());
        assertTrue(actualHandleMethodArgumentTypeMismatchExceptionResult.getHeaders().isEmpty());
    }

    @Test
    void testHandleIncorrectEmailFormatException() {
        UserServiceBadRequestExceptionHandler userServiceExceptionHandler = new UserServiceBadRequestExceptionHandler();
        ResponseEntity<Map<String, Object>> actualHandleIncorrectEmailFormatExceptionResult =
                userServiceExceptionHandler.handleIncorrectEmailFormatException(
                        new IncorrectEmailFormatException("jane.doe@example.org"));
        assertEquals(expectedSize, actualHandleIncorrectEmailFormatExceptionResult.getBody().size());
        assertEquals(badRequest, actualHandleIncorrectEmailFormatExceptionResult.getStatusCodeValue());
        assertTrue(actualHandleIncorrectEmailFormatExceptionResult.hasBody());
        assertTrue(actualHandleIncorrectEmailFormatExceptionResult.getHeaders().isEmpty());
    }

    @Test
    void testHandleInvalidDataException() {
        UserServiceBadRequestExceptionHandler userServiceExceptionHandler = new UserServiceBadRequestExceptionHandler();
        ResponseEntity<Object> actualHandleInvalidDataExceptionResult = userServiceExceptionHandler
                .handleInvalidDataException(new InvalidDataException("foo"));
        assertEquals(expectedSize, ((Map<String, Object>) actualHandleInvalidDataExceptionResult.getBody()).size());
        assertEquals(badRequest, actualHandleInvalidDataExceptionResult.getStatusCodeValue());
        assertTrue(actualHandleInvalidDataExceptionResult.hasBody());
        assertTrue(actualHandleInvalidDataExceptionResult.getHeaders().isEmpty());
    }

    @Test
    void testConstructorWithMessage() {

        String expectedMessage = "Old password is incorrect";

        CustomerOldPwdException exception = new CustomerOldPwdException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String expectedMessage = "Old password is incorrect";
        Throwable cause = new RuntimeException("Internal server error");
        CustomerOldPwdException exception = new CustomerOldPwdException(expectedMessage, cause);
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testHandlePasswordHistoryRetrievalExceptionErrorOccurred() {
        UserServiceBadRequestExceptionHandler userServiceExceptionHandler = new UserServiceBadRequestExceptionHandler();
        ResponseEntity<Object> actualHandlePasswordHistoryRetrievalExceptionResult = userServiceExceptionHandler
                .handlePasswordHistoryRetrievalException(
                        new PasswordHistoryRetrievalException("An error occurred", new Throwable()));
        assertEquals(THREE_CONSTANT, (
                (Map<String, Object>) actualHandlePasswordHistoryRetrievalExceptionResult.getBody())
                .size());
        assertEquals(INTERNAL_ERROR_CODE, actualHandlePasswordHistoryRetrievalExceptionResult.getStatusCodeValue());
        assertTrue(actualHandlePasswordHistoryRetrievalExceptionResult.hasBody());
        assertTrue(actualHandlePasswordHistoryRetrievalExceptionResult.getHeaders().isEmpty());
    }

    @Test
    void testHandlePasswordHistoryRetrievalExceptionNotAllWhoWander() {
        UserServiceBadRequestExceptionHandler userServiceExceptionHandler = new UserServiceBadRequestExceptionHandler();
        ResponseEntity<Object> actualHandlePasswordHistoryRetrievalExceptionResult = userServiceExceptionHandler
                .handlePasswordHistoryRetrievalException(
                        new PasswordHistoryRetrievalException("Not all who wander are lost", new Throwable()));
        assertEquals(THREE_CONSTANT, (
                (Map<String, Object>) actualHandlePasswordHistoryRetrievalExceptionResult.getBody())
                .size());
        assertEquals(INTERNAL_ERROR_CODE, actualHandlePasswordHistoryRetrievalExceptionResult.getStatusCodeValue());
        assertTrue(actualHandlePasswordHistoryRetrievalExceptionResult.hasBody());
        assertTrue(actualHandlePasswordHistoryRetrievalExceptionResult.getHeaders().isEmpty());
    }

    @Test
    void testHandlePasswordHistoryRetrievalExceptionMockedException() {
        UserServiceBadRequestExceptionHandler userServiceExceptionHandler = new UserServiceBadRequestExceptionHandler();
        PasswordHistoryRetrievalException exception = mock(PasswordHistoryRetrievalException.class);
        when(exception.getMessage()).thenReturn("Not all who wander are lost");
        doNothing().when(exception).printStackTrace(Mockito.<PrintWriter>any());
        ResponseEntity<Object> actualHandlePasswordHistoryRetrievalExceptionResult = userServiceExceptionHandler
                .handlePasswordHistoryRetrievalException(exception);
        verify(exception).getMessage();
        verify(exception).printStackTrace(Mockito.<PrintWriter>any());
        assertEquals(THREE_CONSTANT, (
                (Map<String, Object>) actualHandlePasswordHistoryRetrievalExceptionResult.getBody())
                .size());
        assertEquals(INTERNAL_ERROR_CODE, actualHandlePasswordHistoryRetrievalExceptionResult.getStatusCodeValue());
        assertTrue(actualHandlePasswordHistoryRetrievalExceptionResult.hasBody());
        assertTrue(actualHandlePasswordHistoryRetrievalExceptionResult.getHeaders().isEmpty());
    }

}