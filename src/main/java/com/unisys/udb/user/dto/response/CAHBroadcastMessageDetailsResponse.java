package com.unisys.udb.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CAHBroadcastMessageDetailsResponse {
    private String messageId;
    private String messageName;
    private String startDateAndTime;
    private String endDateAndTime;
    private String accountType;
    private String status;
    private Object messageDescription;
}