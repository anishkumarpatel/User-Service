package com.unisys.udb.user.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MarketingNotificationPreferenceResponse {
    private boolean emailNotificationBanking = true;
    private boolean smsNotificationBanking = true;
    private boolean postNotificationMarketing = true;
    private boolean telephoneNotificationMarketing = true;
    private boolean onlineNotificationMarketing = true;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
}
