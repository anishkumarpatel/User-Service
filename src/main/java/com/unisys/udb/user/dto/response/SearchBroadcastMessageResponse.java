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
public class SearchBroadcastMessageResponse {
    private String messageId;
    private String messageName;
    private String status;
}
