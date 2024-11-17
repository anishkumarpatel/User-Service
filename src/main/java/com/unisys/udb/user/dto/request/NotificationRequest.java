package com.unisys.udb.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {

    private UUID digitalCustomerProfileId;
    private String notificationEventTimeStamp;
    private String notificationEventSource;
    private String notificationActivity;
    private String notificationTemplateId;
    private String notificationLanguagePreference;
    private String digitalCustomerDeviceId;
    private Map<String, String> notificationBody;
}
