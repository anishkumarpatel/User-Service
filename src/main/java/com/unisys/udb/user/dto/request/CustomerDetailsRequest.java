package com.unisys.udb.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDetailsRequest {
    private UUID coreCustomerProfileId;
    private String digitalDeviceUdId;
    private String otpEvent;
    private String channel;

}
