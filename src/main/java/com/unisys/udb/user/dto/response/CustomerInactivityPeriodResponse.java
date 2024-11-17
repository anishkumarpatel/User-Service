package com.unisys.udb.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInactivityPeriodResponse {
    private boolean active;
    private String message;
    private String monthDuration;
    private String activityStatus;
    private boolean isUnlockPending;
    private boolean isReAutheticationPending;
}
