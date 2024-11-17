package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.DigitalAlertRequest;
import com.unisys.udb.user.dto.response.DigitalAlertResponse;
import com.unisys.udb.user.dto.response.UserSuccessResponse;
import com.unisys.udb.user.entity.DigitalCustomerAlert;
import com.unisys.udb.user.entity.DigitalDocdbAlertRef;
import com.unisys.udb.user.exception.DigitalAlertNotFoundException;
import com.unisys.udb.user.exception.InvalidDigitalAlertKeyException;
import com.unisys.udb.user.exception.InvalidDigitalCustomerProfileIdException;
import com.unisys.udb.user.repository.DigitalCustomerAlertRepository;
import com.unisys.udb.user.repository.DigitalCustomerProfileRepository;
import com.unisys.udb.user.repository.DigitalDocdbAlertRefRepository;
import com.unisys.udb.user.service.DigitalCustomerAlertService;
import com.unisys.udb.utility.constants.DateFormatType;
import com.unisys.udb.utility.util.DateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class DigitalCustomerAlertServiceImpl implements DigitalCustomerAlertService {

    private final DigitalCustomerAlertRepository digitalCustomerAlertRepository;
    private final DigitalDocdbAlertRefRepository digitalDocdbAlertRefRepository;
    private final DigitalCustomerProfileRepository digitalCustomerProfileRepository;

    /**
     * Retrieves unread digital customer alerts for a given digital customer profile ID.
     *
     * @param digitalCustomerProfileId The ID of the digital customer profile.
     * @return A list of {@link DigitalAlertResponse} objects representing the unread alerts.
     * @throws DataAccessException If an error occurs while accessing the repository.
     */
    @Override
    public List<DigitalAlertResponse> getDigitalCustomerAlerts(
            UUID digitalCustomerProfileId) throws DataAccessException {
        log.debug("Entering getDigitalCustomerAlerts for digitalCustomerProfileId: {}", digitalCustomerProfileId);

        List<DigitalCustomerAlert> customerAlerts = digitalCustomerAlertRepository
                .findByDigitalCustomerProfileIdAndAlertReadFlagOrderByAlertCreationDateDesc(
                        digitalCustomerProfileId, false);

        // Find all alerts with the key "alertInvalidLoginAttempt"
        List<DigitalCustomerAlert> invalidLoginAlerts = customerAlerts.stream()
                .filter(alert -> UdbConstants.ALERT_INVALID_LOGIN_ATTEMPT
                        .equals(alert.getDigitalDocdbAlertRef().getDigitalAlertKey()))
                .toList();

        List<DigitalAlertResponse> responses = new ArrayList<>();

        // If there are invalid login attempts, add the first one with the count as param
        if (!invalidLoginAlerts.isEmpty()) {
            DigitalCustomerAlert firstInvalidLoginAlert = invalidLoginAlerts.get(0);
            DigitalAlertResponse invalidLoginResponse = mapToDigitalAlertResponse(firstInvalidLoginAlert);
            invalidLoginResponse.setParams(Collections.singletonList(String.valueOf(invalidLoginAlerts.size())));
            responses.add(invalidLoginResponse);
        }

        // Add all other alerts except "alertInvalidLoginAttempt"
        customerAlerts.stream()
                .filter(alert -> !UdbConstants.ALERT_INVALID_LOGIN_ATTEMPT
                        .equals(alert.getDigitalDocdbAlertRef().getDigitalAlertKey()))
                .map(this::mapToDigitalAlertResponse)
                .forEach(responses::add);

        log.debug("Returning {} digital alert responses.", responses.size());
        return responses;
    }


    /**
     * Maps a {@link DigitalCustomerAlert} object to a {@link DigitalAlertResponse} object.
     *
     * @param customerAlerts The DigitalCustomerAlert object to be mapped.
     * @return A new DigitalAlertResponse object.
     */
    private DigitalAlertResponse mapToDigitalAlertResponse(DigitalCustomerAlert customerAlerts) {
        log.debug("Mapping DigitalCustomerAlert to DigitalAlertResponse: {}", customerAlerts);
        return new DigitalAlertResponse(
                customerAlerts.getDigitalDocdbAlertRef().getDigitalAlertKey(),
                customerAlerts.isAlertReadFlag(),
                DateUtil.formatDate(customerAlerts.getAlertCreationDate(), DateFormatType.DAY_MONTH_YEAR),
                new ArrayList<>()
        );
    }

    /**
     * Maps a {@link UUID} object to a {@link DigitalAlertResponse} object.
     *
     * @param digitalCustomerProfileId The UUID object to be mapped.
     * @return An Integer object.
     */
    @Override
    public Integer countUnreadUserAlerts(UUID digitalCustomerProfileId) throws DataAccessException {
        log.debug("Counting unread alerts for digital customer profile ID: {}", digitalCustomerProfileId);
        return digitalCustomerAlertRepository.countUnreadAlertsByProfileId(digitalCustomerProfileId);
    }


    /**
     * Marks alerts as read based on the provided alert key and digital customer profile ID.
     *
     * @param alertRequest The request containing the alert key and digital customer profile ID.
     *                     Must not be {@code null}.
     * @return A {@link UserSuccessResponse} indicating the success of the operation.
     * @throws DataAccessException If an error occurs during database access.
     * @throws DigitalAlertNotFoundException If no alerts are found matching the provided key and profile ID.
     */
    @Transactional
    @Override
    public UserSuccessResponse markAlertAsRead(DigitalAlertRequest alertRequest) throws DataAccessException {
        String digitalAlertKey = alertRequest.getAlertKey();
        UUID digitalCustomerProfileId = alertRequest.getDigitalCustomerProfileId();
        log.debug("Entering markAlertAsRead with digitalAlertKey: {} and digitalCustomerProfileId: {}",
                digitalAlertKey, digitalCustomerProfileId);

        // Combine database operations into a single update query (if supported by your database)
        int updatedCount = digitalCustomerAlertRepository.updateAlertReadFlagByProfileIdAndAlertKey(
                digitalCustomerProfileId, false, digitalAlertKey);

        if (updatedCount == 0) {
            throw new DigitalAlertNotFoundException("Alert not found");
        }

        log.debug("Updated alertReadFlag for {} alerts", updatedCount);

        return new UserSuccessResponse("Alerts marked as read successfully");
    }

    /**
     * Saves a digital customer alert based on the provided alert request.
     *
     * @param alertRequest The alert request containing the details of the alert to be saved.
     * @throws InvalidDigitalAlertKeyException    If the digital alert key is invalid or not found.
     */
    @Override
    public void saveDigitalCustomerAlert(DigitalAlertRequest alertRequest) {
        UUID digitalCustomerProfileId = alertRequest.getDigitalCustomerProfileId();
        String digitalAlertKey = alertRequest.getAlertKey();

        log.debug("Received Alert Request: digitalCustomerProfileId={}, digitalAlertKey={}",
                digitalCustomerProfileId, digitalAlertKey);

        // Check for digitalCustomerProfileId
        if (Boolean.FALSE.equals(digitalCustomerProfileRepository.existsByDigitalCustomerProfileId(
                digitalCustomerProfileId))) {
            throw new InvalidDigitalCustomerProfileIdException("Invalid digital customer profile id");
        }

        // Find docDbAlertRef by digitalAlertKey
        DigitalDocdbAlertRef docDbAlertRef = Optional.ofNullable(digitalDocdbAlertRefRepository.findByDigitalAlertKey(
                        digitalAlertKey))
                .orElseThrow(() -> new InvalidDigitalAlertKeyException("Invalid digital alert key"));

        log.debug("Found docDbAlertRef: {}", docDbAlertRef);

        // Create and save new alert
        DigitalCustomerAlert newAlert = new DigitalCustomerAlert();
        newAlert.setDigitalCustomerProfileId(digitalCustomerProfileId);
        newAlert.setAlertReadFlag(false);
        newAlert.setDigitalDocdbAlertRef(docDbAlertRef);
        newAlert.setAlertCreatedBy("SYSTEM");
        newAlert.setAlertCreationDate(LocalDateTime.now());

        digitalCustomerAlertRepository.save(newAlert);
    }

}
