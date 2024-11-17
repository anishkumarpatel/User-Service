package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.response.CustomerInactivityPeriodResponse;
import com.unisys.udb.user.dto.response.GlobalConfigResponse;
import com.unisys.udb.user.dto.response.UserActivityStatusResponse;
import com.unisys.udb.user.exception.ConfigurationServiceException;
import com.unisys.udb.user.exception.ConfigurationServiceUnavailableException;
import com.unisys.udb.user.exception.WebClientIntegrationException;
import com.unisys.udb.user.exception.response.UdbExceptionResponse;
import com.unisys.udb.user.repository.DigitalCustomerActivityRepository;
import com.unisys.udb.user.repository.DigitalCustomerProfileRepository;
import com.unisys.udb.user.service.UserRegistrationService;
import com.unisys.udb.user.service.client.ConfigurationServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.unisys.udb.user.constants.UdbConstants.REAUTHENTICATION_ACTIVITY_NAME;
import static com.unisys.udb.user.constants.UdbConstants.STATUS_UNLOCK_PENDING;
import static com.unisys.udb.user.constants.UdbConstants.THREE;
import static com.unisys.udb.user.constants.UdbConstants.UDB_REAUTHENTICATION_DURATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.unisys.udb.user.constants.UdbConstants.FIFTY;

class
DigitalCustomerActivityServiceImplTest {

    @Mock
    private DigitalCustomerActivityRepository digitalCustomerActivityRepository;

    @Mock
    private DigitalCustomerProfileRepository digitalCustomerProfileRepository;

    @Mock
    private ConfigurationServiceClient configurationServiceClient;

    @Mock
    private UserRegistrationService userRegistrationService;
    @InjectMocks
    private DigitalCustomerActivityServiceImpl digitalCustomerActivityService;

    private UUID digitalCustomerProfileId;

    private Method handleWebClientException;


    @BeforeEach
    void setUp() throws NoSuchMethodException  {
        // Initialize a UUID for the test cases
        digitalCustomerProfileId = UUID.randomUUID();
        MockitoAnnotations.openMocks(this);
        handleWebClientException = DigitalCustomerActivityServiceImpl.class.getDeclaredMethod(
                "handleWebClientException", WebClientResponseException.class);
        handleWebClientException.setAccessible(true);
    }

    @Test
    void testHasCustomerExceededInactivityPeriodShouldReturnActiveTrueWithinInactivityPeriod() {
        // Arrange
        when(configurationServiceClient.getGlobalConfig(UDB_REAUTHENTICATION_DURATION))
                .thenReturn(List.of(new GlobalConfigResponse("Month", "3")));
        LocalDateTime pastDate = LocalDateTime.now().minusDays(FIFTY); // 50 days ago
        when(digitalCustomerActivityRepository.getCustomerLastLoginActivityTime(digitalCustomerProfileId))
                .thenReturn(pastDate);
        when(digitalCustomerActivityRepository.findCustomerRecentActivityStatusByActivityName(
                digitalCustomerProfileId, REAUTHENTICATION_ACTIVITY_NAME, THREE)).thenReturn("success");


        // Act
        CustomerInactivityPeriodResponse result = digitalCustomerActivityService
                .checkCustomerInactivityPeriod(digitalCustomerProfileId);

        // Assert
        assertTrue(result.isActive(), "Expected the inactivity period to be within the limit");
    }

    @Test
    void testHasCustomerExceededInactivityPeriodShouldReturnActiveTrueNoActivityTimeFound() {
        // Arrange
        when(configurationServiceClient.getGlobalConfig(UDB_REAUTHENTICATION_DURATION))
                .thenReturn(List.of(new GlobalConfigResponse("Month", "3")));
        when(digitalCustomerActivityRepository.getCustomerLastLoginActivityTime(digitalCustomerProfileId))
                .thenReturn(null);
        when(digitalCustomerActivityRepository.findCustomerRecentActivityStatusByActivityName(
                digitalCustomerProfileId, REAUTHENTICATION_ACTIVITY_NAME, THREE)).thenReturn(null);

        // Act
        CustomerInactivityPeriodResponse result = digitalCustomerActivityService
                .checkCustomerInactivityPeriod(digitalCustomerProfileId);

        // Assert
        assertTrue(result.isActive(), "Expected the method to return false when no activity time is found");
        assertNull(result.getActivityStatus());

    }


    @Test
    void testHasCustomerExceededInactivityPeriodShouldReturnActiveFalseWhenExceedsInactivityPeriod() {
        // Arrange
        LocalDateTime pastDate = LocalDateTime.now().minusMonths(THREE); // 100 days ago
        when(configurationServiceClient.getGlobalConfig(UDB_REAUTHENTICATION_DURATION))
                .thenReturn(List.of(new GlobalConfigResponse("Month", "3")));
        when(digitalCustomerActivityRepository.getCustomerLastLoginActivityTime(digitalCustomerProfileId))
                .thenReturn(pastDate);
        when(userRegistrationService.updatePinStatus(digitalCustomerProfileId, false))
                .thenReturn(null);

        // Act
        CustomerInactivityPeriodResponse result = digitalCustomerActivityService
                .checkCustomerInactivityPeriod(digitalCustomerProfileId);

        // Assert
        assertFalse(result.isActive(), "Expected the inactivity period to be exceeded");
        verify(digitalCustomerActivityRepository).getCustomerLastLoginActivityTime(digitalCustomerProfileId);
    }

    @Test
    void testHasCustomerActivityStatusIsReAuthenticationShouldReturnFalse() {
        // Arrange
        when(configurationServiceClient.getGlobalConfig(UDB_REAUTHENTICATION_DURATION))
                .thenReturn(List.of(new GlobalConfigResponse("Month", "3")));
        LocalDateTime pastDate = LocalDateTime.now().minusDays(FIFTY); // 50 days ago
        when(digitalCustomerActivityRepository.getCustomerLastLoginActivityTime(digitalCustomerProfileId))
                .thenReturn(pastDate);
        when(digitalCustomerActivityRepository.findCustomerRecentActivityStatusByActivityName(
                digitalCustomerProfileId, REAUTHENTICATION_ACTIVITY_NAME, THREE)).thenReturn("reAuthPin");
        when(userRegistrationService.updatePinStatus(digitalCustomerProfileId, false))
                .thenReturn(null);

        // Act
        CustomerInactivityPeriodResponse result = digitalCustomerActivityService
                .checkCustomerInactivityPeriod(digitalCustomerProfileId);

        // Assert
        assertFalse(result.isActive(), "Expected the inactivity period to be within the limit");
        assertEquals("reAuthPin", result.getActivityStatus());
    }

    @Test
    void testHasCustomerStatusUnlockPendingIsActiveReturnFalse() {
        // Arrange
        when(configurationServiceClient.getGlobalConfig(UDB_REAUTHENTICATION_DURATION))
                .thenReturn(List.of(new GlobalConfigResponse("Month", "3")));
        LocalDateTime pastDate = LocalDateTime.now().minusDays(THREE); // 50 days ago
        when(digitalCustomerActivityRepository.getCustomerLastLoginActivityTime(digitalCustomerProfileId))
                .thenReturn(pastDate);
        when(digitalCustomerActivityRepository.findCustomerRecentActivityStatusByActivityName(
                digitalCustomerProfileId, REAUTHENTICATION_ACTIVITY_NAME, THREE)).thenReturn("success");
        when(digitalCustomerProfileRepository.existsByDigitalCustomerProfileIdAndCustomerStatusType(
                digitalCustomerProfileId, STATUS_UNLOCK_PENDING)).thenReturn(true);
        when(userRegistrationService.updatePinStatus(digitalCustomerProfileId, false))
                .thenReturn(null);

        // Act
        CustomerInactivityPeriodResponse result = digitalCustomerActivityService
                .checkCustomerInactivityPeriod(digitalCustomerProfileId);

        // Assert
        assertFalse(result.isActive(), "Expected the inactivity period to be within the limit");
        assertTrue(result.isUnlockPending());
    }

    @Test
    void testHandleWebClientExceptionInternalServerError() throws Throwable {
        WebClientResponseException webClientException = mock(WebClientResponseException.class);
        when(webClientException.getMessage()).thenReturn(UdbConstants.INTERNAL_SERVER_ERROR_CODE);

        // Act & Assert
        assertThrows(ConfigurationServiceException.class, () -> invokeHandleWebClientException(webClientException));
    }

    @Test
    void testHandleWebClientExceptionServerUnavailable() throws Throwable {
        WebClientResponseException webClientException = mock(WebClientResponseException.class);
        when(webClientException.getMessage()).thenReturn(UdbConstants.SERVICE_UNAVAILABLE);

        // Act & Assert
        assertThrows(ConfigurationServiceUnavailableException.class,
                () -> invokeHandleWebClientException(webClientException));
    }

    @Test
    void testHandleWebClientExceptionIntegrationException() throws Throwable {
        // Arrange
        WebClientResponseException webClientException = mock(WebClientResponseException.class);
        when(webClientException.getMessage()).thenReturn("OTHER_ERROR");

        // Create a mock UdbExceptionResponse
        UdbExceptionResponse mockResponse = mock(UdbExceptionResponse.class);
        when(webClientException.getResponseBodyAs(UdbExceptionResponse.class)).thenReturn(mockResponse);

        when(webClientException.getStatusCode()).thenReturn(HttpStatus.BAD_GATEWAY);
        // Act & Assert
        assertThrows(WebClientIntegrationException.class, () -> invokeHandleWebClientException(webClientException));
    }

    @Test
    void testCheckCustomerInactivityPeriodException() {
        WebClientResponseException webClientException = mock(WebClientResponseException.class);
        when(webClientException.getMessage()).thenReturn(UdbConstants.SERVICE_UNAVAILABLE);
        when(configurationServiceClient.getGlobalConfig(UDB_REAUTHENTICATION_DURATION))
                .thenThrow(webClientException);

        assertThrows(ConfigurationServiceUnavailableException.class, () -> digitalCustomerActivityService
                .checkCustomerInactivityPeriod(digitalCustomerProfileId));

    }

    private void invokeHandleWebClientException(WebClientResponseException exception) throws Throwable {
        try {
            handleWebClientException.invoke(digitalCustomerActivityService, exception);
        } catch (InvocationTargetException e) {
            throw e.getCause(); // Ensure the original cause is thrown
        }
    }


    @Test
    void testGetUserRecentReAuthenticationActivityStatusShouldReturnActivityStatus() {
        // Given
        String expectedActivityStatus = "ACTIVE";
        when(digitalCustomerActivityRepository
                .findCustomerRecentActivityStatusByActivityName(
                        digitalCustomerProfileId, REAUTHENTICATION_ACTIVITY_NAME, THREE))
                .thenReturn(expectedActivityStatus);

        // When
        UserActivityStatusResponse response = digitalCustomerActivityService
                .getUserRecentReAuthenticationActivityStatus(digitalCustomerProfileId);

        // Then
        assertNotNull(response);
        assertEquals(expectedActivityStatus, response.getActivityStatus());
        verify(digitalCustomerActivityRepository, times(1))
                .findCustomerRecentActivityStatusByActivityName(
                        digitalCustomerProfileId, REAUTHENTICATION_ACTIVITY_NAME, THREE);
    }

    @Test
    void testGetUserRecentReAuthenticationActivityStatusWhenNoActivityFoundShouldReturnNullActivityStatus() {
        // Given
        when(digitalCustomerActivityRepository
                .findCustomerRecentActivityStatusByActivityName(
                        digitalCustomerProfileId, REAUTHENTICATION_ACTIVITY_NAME, THREE))
                .thenReturn(null);

        // When
        UserActivityStatusResponse response = digitalCustomerActivityService
                .getUserRecentReAuthenticationActivityStatus(digitalCustomerProfileId);

        // Then
        assertNotNull(response);
        assertNull(response.getActivityStatus());
        verify(digitalCustomerActivityRepository, times(1))
                .findCustomerRecentActivityStatusByActivityName(
                        digitalCustomerProfileId, REAUTHENTICATION_ACTIVITY_NAME, THREE);
    }
}
