package com.unisys.udb.user.utils.dto.response;

import com.unisys.udb.user.dto.request.NotificationOrchestratorRequest;
import com.unisys.udb.user.dto.response.DeviceInfoResponse;
import com.unisys.udb.user.dto.response.NotificationOrchestratorResponse;
import com.unisys.udb.user.exception.DigitalCustomerDeviceNotFoundException;
import com.unisys.udb.user.exception.NotificationRequestInputFieldException;
import com.unisys.udb.user.exception.PublishNotificationFailureException;
import com.unisys.udb.user.repository.DigitalCustomerDeviceRepository;
import com.unisys.udb.user.repository.DigitalCustomerStatus;
import com.unisys.udb.user.service.client.NotificationOrchestratorServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.unisys.udb.user.constants.UdbConstants.*;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Slf4j
@Component
public class NotificationUtil {
    private final NotificationOrchestratorServiceClient notificationOrchestratorServiceClient;
    private final DigitalCustomerDeviceRepository digitalCustomerDeviceRepository;

    public void sendNotification(final Map<String, String> requiredFieldsMap,
                                 final Map<String, String> extendedFieldsMap) {
        try {
            NotificationOrchestratorRequest notificationRequest = new NotificationOrchestratorRequest();
            notificationRequest.setRequiredFieldsMap(requiredFieldsMap);
            notificationRequest.setExtendedFieldsMap(extendedFieldsMap);
            log.info("Invoking notification orchestrator service api for sending notification");

            NotificationOrchestratorResponse orchestratorResponse = notificationOrchestratorServiceClient
                    .publishNotification(notificationRequest);
            log.info("step = sendNotification, action = Notification status for the activity {} : {}",
                    requiredFieldsMap.get(NOTIFICATION_ACTIVITY), orchestratorResponse);
        } catch (WebClientResponseException exception) {
            log.error("Failed to send notification due to {}", exception.getResponseBodyAsString());
            throw new PublishNotificationFailureException(exception.getResponseBodyAsString());
        }
    }



    public Map<String, String> prepareRequiredFieldsMap(final UUID digitalCustomerProfileId,
                                                        final String digitalUserName, final String eventSource,
                                                        final String activity, final String templateName,
                                                        final String languagePreference) {
        Map<String, String> mandatoryFields = new HashMap<>();
        if (nonNull(digitalCustomerProfileId)) {
            mandatoryFields.put(DIGITAL_CUSTOMER_PROFILE_ID, String.valueOf(digitalCustomerProfileId));
        } else if (nonNull(digitalUserName)) {
            mandatoryFields.put(DIGITAL_USER_NAME, digitalUserName);
        } else {
            throw new NotificationRequestInputFieldException(DIGITAL_CUSTOMER_PROFILE_ID + " or "
                    + DIGITAL_USER_NAME + "required as input");
        }
        String formattedTime = CommonUtil.getCurrentFormattedTime();
        mandatoryFields.put(NOTIFICATION_EVENT_TIMESTAMP, formattedTime);
        mandatoryFields.put(NOTIFICATION_EVENT_SOURCE, eventSource);
        mandatoryFields.put(NOTIFICATION_ACTIVITY, activity);
        mandatoryFields.put(NOTIFICATION_TEMPLATE_NAME, templateName);
        DeviceInfoResponse response = getUserDeviceInfo(digitalCustomerProfileId);
        mandatoryFields.put(NOTIFICATION_DIGITAL_CUSTOMER_DEVICE_ID,
                String.valueOf(response.getDigitalCustomerDeviceId()));
        mandatoryFields.put(NOTIFICATION_LANGUAGE_PREFERENCE, languagePreference);
        return mandatoryFields;
    }

    public DeviceInfoResponse getUserDeviceInfo(final UUID digitalCustomerProfileId) {
        log.info("Fetching device info for digital customer profile ID: {}", digitalCustomerProfileId);

        List<DigitalCustomerStatus> result = digitalCustomerDeviceRepository
                .findByProfileIdAndStatus(digitalCustomerProfileId);
        return result.stream()
                .findFirst()
                .map(digitalCustomerDevice -> DeviceInfoResponse.builder()
                        .digitalCustomerDeviceId(digitalCustomerDevice.getDeviceId())
                        .deviceType(digitalCustomerDevice.getDeviceType())
                        .deviceToken(digitalCustomerDevice.getDeviceToken())
                        .build())
                .orElseThrow(() -> {
                    log.warn("Device info not found for digital customer profile ID: {}", digitalCustomerProfileId);
                    return new DigitalCustomerDeviceNotFoundException(digitalCustomerProfileId);
                });
    }
}