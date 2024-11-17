package com.unisys.udb.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationPreferenceResponse {
    private String notificationTypeElementName;
    private String notificationDescElementName;
    private Boolean notificationFlag;
}
