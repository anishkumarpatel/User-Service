package com.unisys.udb.user.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import com.unisys.udb.user.dto.response.NotificationOrchestratorResponse;
import com.unisys.udb.user.exception.PublishNotificationFailureException;
import com.unisys.udb.user.repository.DigitalCustomerDeviceRepository;
import com.unisys.udb.user.repository.DigitalCustomerStatus;
import com.unisys.udb.user.service.client.NotificationOrchestratorServiceClient;
import com.unisys.udb.user.utils.dto.response.CommonUtil;
import com.unisys.udb.user.utils.dto.response.NotificationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class NotificationUtilTest {

    @Mock
    private NotificationOrchestratorServiceClient notificationOrchestratorServiceClient;

    @Mock
    private DigitalCustomerDeviceRepository digitalCustomerDeviceRepository;

    @Mock
    private CommonUtil commonUtil;

    @InjectMocks
    private NotificationUtil notificationUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPrepareRequiredFieldsMapSuccessUnlock() {
        UUID profileId = UUID.randomUUID();
        DigitalCustomerStatus digitalCustomerStatus = mock(DigitalCustomerStatus.class);
        when(digitalCustomerStatus.getDeviceId()).thenReturn(1);
        when(digitalCustomerStatus.getDeviceType()).thenReturn("type");
        when(digitalCustomerStatus.getDeviceToken()).thenReturn("tokengfdhd");
        when(digitalCustomerDeviceRepository.findByProfileIdAndStatus(profileId))
                .thenReturn(List.of(digitalCustomerStatus));

        Map<String, String> result = notificationUtil.prepareRequiredFieldsMap(
                profileId, null, "USER_SERVICE", "DIGITAL_ACCESS_UNLOCKED",
                "3", "LANGUAGE_PREFERENCE");

        verify(digitalCustomerDeviceRepository).findByProfileIdAndStatus(Mockito.any());
        assertNotNull(result);
        assertEquals(null, result.get("NOTIFICATION_DIGITAL_CUSTOMER_DEVICE_ID"));
    }

    @Test
    void testSendNotificationSuccess() {
        NotificationOrchestratorResponse response = new NotificationOrchestratorResponse();
        when(notificationOrchestratorServiceClient.publishNotification(any())).thenReturn(response);

        Map<String, String> requiredFieldsMap = new HashMap<>();
        Map<String, String> extendedFieldsMap = new HashMap<>();
        notificationUtil.sendNotification(requiredFieldsMap, extendedFieldsMap);

        verify(notificationOrchestratorServiceClient).publishNotification(any());
    }

    @Test
    void testSendNotificationFailure() {
        WebClientResponseException exception = mock(WebClientResponseException.class);
        when(exception.getResponseBodyAsString()).thenReturn("Error message");
        when(notificationOrchestratorServiceClient.publishNotification(any())).thenThrow(exception);

        Map<String, String> requiredFieldsMap = new HashMap<>();
        Map<String, String> extendedFieldsMap = new HashMap<>();

        try {
            notificationUtil.sendNotification(requiredFieldsMap, extendedFieldsMap);
        } catch (PublishNotificationFailureException e) {
            assertEquals("Error message", e.getMessage());
        }

        verify(notificationOrchestratorServiceClient).publishNotification(any());
    }
}
