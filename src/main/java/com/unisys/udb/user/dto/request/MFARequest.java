package com.unisys.udb.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MFARequest {
    private String mfaAction;
    private String deviceId;
    private String userName;
}
