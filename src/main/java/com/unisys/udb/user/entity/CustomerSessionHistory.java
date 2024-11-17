package com.unisys.udb.user.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@ToString
public class CustomerSessionHistory {
    private String activityName;
    private String activityDate;
    private String activityTime;
    private String activityChannel;
    private String activityPlatform;

    public CustomerSessionHistory(String activityName,
                                  String activityDate, String activityTime, String activityChannel,
                                  String activityPlatform) {
        this.activityName = activityName;
        this.activityDate = activityDate;
        this.activityTime = activityTime;
        this.activityChannel = activityChannel;
        this.activityPlatform = activityPlatform;
    }
}
