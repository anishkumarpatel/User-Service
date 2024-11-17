package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.dto.request.ReAuthenticateActivityRequest;
import com.unisys.udb.user.entity.DigitalCustomerActivityEntity;
import com.unisys.udb.user.entity.DigitalCustomerDevice;
import com.unisys.udb.user.repository.DigitalActivityDetailRefRepository;
import com.unisys.udb.user.repository.DigitalCustomerActivityRepository;
import com.unisys.udb.user.repository.DigitalCustomerDeviceRepository;
import com.unisys.udb.user.service.ReAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.unisys.udb.user.constants.UdbConstants.REAUTHENTICATION_RECORDED;
import static com.unisys.udb.user.constants.UdbConstants.REAUTHORIZATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReAuthenticationServiceImpl implements ReAuthenticationService {

    private final DigitalCustomerDeviceRepository digitalCustomerDeviceRepository;
    private final DigitalActivityDetailRefRepository digitalActivityDetailRefRepository;
    private final DigitalCustomerActivityRepository digitalCustomerActivityRepository;


    /**
     * Adds a re-authentication activity for a digital customer profile.
     *
     * This method processes the re-authentication request by performing the following steps:
     * 1. Retrieves the `deviceId` based on the `digitalCustomerProfileId` from the request.
     * 2. Fetches the corresponding `digitalActivityDetailRefId` for the "REAUTHORIZATION" activity.
     * 3. Creates and populates a new `DigitalCustomerActivityEntity` with details from the request,
     *    such as activity status, time, and channel, along with the retrieved `deviceId`
     *    and `digitalActivityDetailRefId`.
     * 4. Retrieves the latest `activityCorrelationKey` based on the profile's activity history.
     * 5. Saves the new activity entity to the `digitalCustomerActivityRepository`.
     *
     * @param reAuthenticateActivityRequest The re-authentication activity request containing profileId,
     *                                     status, and channel.
     * @return A string indicating that the re-authentication activity has been recorded.
     */
    @Override
    public String addReAuthenticateActivity(ReAuthenticateActivityRequest reAuthenticateActivityRequest) {
        // Log start of the method
        log.debug("Start re-authenticate activity for profileId: ");

        int deviceId = digitalCustomerDeviceRepository
                .findByDigitalDeviceUdid(reAuthenticateActivityRequest.getDigitalDeviceUdid())
                .map(DigitalCustomerDevice::getDigitalCustomerDeviceId)
                .orElseThrow();

        int digitalActivityDetailRefId = digitalActivityDetailRefRepository
                .findByDigitalActivityName(REAUTHORIZATION)
                .getDigitalActivityDetailRefId();

        DigitalCustomerActivityEntity digitalCustomerActivityEntity = digitalCustomerActivityRepository
                .findTopByDigitalCustomerDeviceIdOrderByActivityTimeDesc(deviceId);

        // Creating a new activity entity
        DigitalCustomerActivityEntity activity = new DigitalCustomerActivityEntity();
        activity.setDigitalCustomerProfileId(digitalCustomerActivityEntity.getDigitalCustomerProfileId());
        activity.setDigitalCustomerDeviceId(deviceId);
        activity.setDigitalActivityDetailRefId(digitalActivityDetailRefId);
        activity.setActivityStatus(reAuthenticateActivityRequest.getStatus());
        activity.setActivityTime(LocalDateTime.now());
        activity.setActivityChannel(reAuthenticateActivityRequest.getChannel());
        activity.setActivityCorelationKey(digitalCustomerActivityEntity.getActivityCorelationKey());

        // Save the activity in the repository
        digitalCustomerActivityRepository.save(activity);
        // Log the successful completion of activity recording
        log.debug("Re-authentication activity recorded for profileId: {}",
                digitalCustomerActivityEntity.getDigitalCustomerProfileId());
        return REAUTHENTICATION_RECORDED;
    }
}
