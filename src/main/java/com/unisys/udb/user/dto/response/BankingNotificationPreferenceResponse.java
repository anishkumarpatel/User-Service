package com.unisys.udb.user.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BankingNotificationPreferenceResponse {
    private boolean mobilePushNotificationBanking = true;
    private boolean emailNotificationBanking = true;
    private boolean smsNotificationBanking = true;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
}
