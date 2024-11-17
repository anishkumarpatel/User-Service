package com.unisys.udb.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class CAHBroadcastResponse {
    private String messageId;
    private String messageName;
    private String startDateAndTime;
    private String endDateAndTime;
    private String accountType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String modificationDate;
}
