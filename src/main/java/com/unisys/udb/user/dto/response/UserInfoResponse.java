package com.unisys.udb.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {

    private UUID digitalCustomerProfileId;
    private UUID coreCustomerProfileId;
    private String digitalUserName;
    private int digitalCustomerStatusTypeId;
    private String digitalCustomerDeviceId;
}
