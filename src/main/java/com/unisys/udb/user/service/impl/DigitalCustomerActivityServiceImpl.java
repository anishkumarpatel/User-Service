package com.unisys.udb.user.service.impl;


import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.response.CustomerInactivityPeriodResponse;
import com.unisys.udb.user.dto.response.UserActivityStatusResponse;
import com.unisys.udb.user.exception.ConfigurationServiceException;
import com.unisys.udb.user.exception.ConfigurationServiceUnavailableException;
import com.unisys.udb.user.exception.WebClientIntegrationException;
import com.unisys.udb.user.exception.response.UdbExceptionResponse;
import com.unisys.udb.user.repository.DigitalCustomerActivityRepository;
import com.unisys.udb.user.repository.DigitalCustomerProfileRepository;
import com.unisys.udb.user.service.DigitalCustomerActivityService;
import com.unisys.udb.user.service.UserRegistrationService;
import com.unisys.udb.user.service.client.ConfigurationServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.unisys.udb.user.constants.UdbConstants.CONFIGURATION_SERVICE_RETRY_MSG;
import static com.unisys.udb.user.constants.UdbConstants.CUSTOMER_ACTIVE_PERIOD_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.CUSTOMER_INACTIVE_PERIOD_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.CUSTOMER_UNLOCK_PENDING_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.FAILURE_RESPONSE_TYPE;
import static com.unisys.udb.user.constants.UdbConstants.INTERNAL_SERVER_ERROR_CODE;
import static com.unisys.udb.user.constants.UdbConstants.REAUTHENTICATION_ACTIVITY_NAME;
import static com.unisys.udb.user.constants.UdbConstants.REAUTHENTICATION_ACTIVITY_STATUS_SUCCESS;
import static com.unisys.udb.user.constants.UdbConstants.SERVICE_UNAVAILABLE;
import static com.unisys.udb.user.constants.UdbConstants.STATUS_UNLOCK_PENDING;
import static com.unisys.udb.user.constants.UdbConstants.THREE;
import static com.unisys.udb.user.constants.UdbConstants.UDB_REAUTHENTICATION_DURATION;
import static java.util.Objects.isNull;


@Slf4j
@Service
@RequiredArgsConstructor
public class DigitalCustomerActivityServiceImpl implements DigitalCustomerActivityService {

    private final ConfigurationServiceClient configurationServiceClient;
    private final DigitalCustomerActivityRepository digitalCustomerActivityRepository;
    private final DigitalCustomerProfileRepository digitalCustomerProfileRepository;
    private final UserRegistrationService userRegistrationService;

    /**
     * Checks whether the customer with the provided digitalCustomerProfileId has exceeded
     * the configured inactivity period. This method retrieves the customer's most recent
     * activity and compares it with the current date, adjusted by the inactivity period (in months)
     * fetched from global configuration.
     *
     * @param digitalCustomerProfileId the unique identifier of the customer's digital profile
     * @return CustomerInactivityPeriodResponse which indicates whether the customer is active
     *         or inactive based on the inactivity period.
     */
    @Override
    public CustomerInactivityPeriodResponse checkCustomerInactivityPeriod(UUID digitalCustomerProfileId) {
        String globalConfigReauthDuration = null;
        CustomerInactivityPeriodResponse customerInactivityPeriodResponse = CustomerInactivityPeriodResponse
                .builder()
                .build();

        try {
            log.debug("Fetched global config responses for {}", UDB_REAUTHENTICATION_DURATION);
            globalConfigReauthDuration = configurationServiceClient.getGlobalConfig(UDB_REAUTHENTICATION_DURATION)
                    .get(0).getDefaultValue();
        } catch (WebClientResponseException ex) {
            handleWebClientException(ex);
        }
        log.debug("Checking if customer with ID {} exceeded inactivity period of months {}",
                digitalCustomerProfileId, globalConfigReauthDuration);
        LocalDateTime resentActivityTime = digitalCustomerActivityRepository
                .getCustomerLastLoginActivityTime(digitalCustomerProfileId);
        if (isNull(resentActivityTime)) {
            log.debug("No recent activity found for customer ID: {}. Assuming customer is active.",
                    digitalCustomerProfileId);
            customerInactivityPeriodResponse.setActive(true);
            customerInactivityPeriodResponse.setMessage(CUSTOMER_ACTIVE_PERIOD_MESSAGE);
            return customerInactivityPeriodResponse;
        }
        LocalDateTime adjustedDate  = resentActivityTime.plusMonths(Long.parseLong(globalConfigReauthDuration));
        String reAuthActivityStatus = digitalCustomerActivityRepository.findCustomerRecentActivityStatusByActivityName(
                        digitalCustomerProfileId, REAUTHENTICATION_ACTIVITY_NAME,
                Integer.parseInt(globalConfigReauthDuration));
        boolean isDateExceeded = adjustedDate.isBefore(LocalDateTime.now())
                || adjustedDate.isEqual(LocalDateTime.now());
        boolean isReAutheticationPending = (reAuthActivityStatus != null
                && !REAUTHENTICATION_ACTIVITY_STATUS_SUCCESS.equals(reAuthActivityStatus));
        boolean isUserStatusUnlockPending = digitalCustomerProfileRepository
                .existsByDigitalCustomerProfileIdAndCustomerStatusType(
                        digitalCustomerProfileId, STATUS_UNLOCK_PENDING);
        if (isDateExceeded || isUserStatusUnlockPending || isReAutheticationPending) {
            userRegistrationService.updatePinStatus(digitalCustomerProfileId, false);
        }
        if (isDateExceeded) {
            log.debug("Customer with ID {} has exceeded the inactivity period. Marking as inactive.",
                    digitalCustomerProfileId);
            markAsInactive(CUSTOMER_INACTIVE_PERIOD_MESSAGE, globalConfigReauthDuration, null,
                    false, customerInactivityPeriodResponse, false);
        } else if (isUserStatusUnlockPending) {
            log.debug("Customer with ID {} has status unlock pending. Marking as inactive.", digitalCustomerProfileId);
            markAsInactive(CUSTOMER_UNLOCK_PENDING_MESSAGE, null,
                    isReAutheticationPending ? reAuthActivityStatus : null,
                    true, customerInactivityPeriodResponse, isReAutheticationPending);
        } else if (isReAutheticationPending) {
            log.debug("Customer with ID {} has not completed Re-Authentication. Marking as inactive.",
                    digitalCustomerProfileId);
            markAsInactive(CUSTOMER_INACTIVE_PERIOD_MESSAGE, globalConfigReauthDuration, reAuthActivityStatus,
                    false, customerInactivityPeriodResponse, isReAutheticationPending);
        } else {
            log.debug("Customer with ID {} is still within the allowed inactivity period. Marking as active.",
                    digitalCustomerProfileId);
            customerInactivityPeriodResponse.setActive(true);
            customerInactivityPeriodResponse.setMessage(CUSTOMER_ACTIVE_PERIOD_MESSAGE);
        }
        // Check and return customerInactivityPeriodResponse if the difference exceeds the configured inactivity period
        return customerInactivityPeriodResponse;
    }

    /**
     * Retrieves the recent activity status of a customer based on the activity name.
     * This method queries the {@link DigitalCustomerActivityRepository} to fetch
     * the status of the most recent activity for the given customer ID and activity name.
     * The result is then returned as a {@link UserActivityStatusResponse}.
     *
     * @param digitalCustomerProfileId the unique identifier of the customer whose activity status is to be fetched
     * @return {@link UserActivityStatusResponse} containing the activity status
     */
    @Override
    public UserActivityStatusResponse getUserRecentReAuthenticationActivityStatus(UUID digitalCustomerProfileId) {
        log.debug("Fetching recent Re-authentication activity status for customer ID: {} ", digitalCustomerProfileId);
        return UserActivityStatusResponse.builder()
                .activityStatus(digitalCustomerActivityRepository
                        .findCustomerRecentActivityStatusByActivityName(
                                digitalCustomerProfileId, REAUTHENTICATION_ACTIVITY_NAME, THREE))
                .build();
    }

    /**
     * Handles exceptions thrown by WebClient during service calls. Depending on the exception
     * message, this method throws specific custom exceptions such as
     * {@link ConfigurationServiceUnavailableException} or {@link ConfigurationServiceException}.
     * If the error does not match predefined cases, it parses the error response and throws
     * a {@link WebClientIntegrationException} with the appropriate status code.
     *
     * @param webClientException the exception caught during the WebClient call
     * @return List<Object> a list containing formatted error parameters for exception handling
     * @throws ConfigurationServiceUnavailableException if the service is unavailable
     * @throws ConfigurationServiceException for internal server errors in the configuration service
     * @throws WebClientIntegrationException for any other error returned by the WebClient
     */
    private List<Object> handleWebClientException(WebClientResponseException webClientException) {
        List<String> params = new ArrayList<>();
        params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT)));
        if (webClientException.getMessage().contains(SERVICE_UNAVAILABLE)) {
            throw new ConfigurationServiceUnavailableException(SERVICE_UNAVAILABLE,
                    HttpStatus.SERVICE_UNAVAILABLE, FAILURE_RESPONSE_TYPE,
                    CONFIGURATION_SERVICE_RETRY_MSG, params);
        } else if (webClientException.getMessage().contains(INTERNAL_SERVER_ERROR_CODE)) {
            throw new ConfigurationServiceException(
                    INTERNAL_SERVER_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR, FAILURE_RESPONSE_TYPE,
                    CONFIGURATION_SERVICE_RETRY_MSG, params);
        } else {
            UdbExceptionResponse exceptionResponse = webClientException.getResponseBodyAs(UdbExceptionResponse.class);
            throw new WebClientIntegrationException(exceptionResponse, webClientException.getStatusCode());
        }
    }

    /**
     * Marks the customer as inactive and updates the response object with provided details.
     *
     * @param message        The message to be set in the response.
     * @param monthDuration  The duration (in months) to be set in the response.
     * @param activityStatus The status of the customer's activity to be set in the response.
     * @param unlockPending  Boolean flag indicating whether unlock is pending.
     * @param response       The response object that will be updated with the inactive status and details.
     */
    private void markAsInactive(String message, String monthDuration, String activityStatus, boolean unlockPending,
            CustomerInactivityPeriodResponse response, boolean isReAutheticationPending) {
        response.setActive(false);
        response.setMessage(message);
        response.setMonthDuration(monthDuration);
        response.setActivityStatus(activityStatus);
        response.setUnlockPending(unlockPending);
        response.setReAutheticationPending(isReAutheticationPending);
    }
}
