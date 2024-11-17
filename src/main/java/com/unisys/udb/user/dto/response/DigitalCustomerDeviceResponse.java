package com.unisys.udb.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DigitalCustomerDeviceResponse {
    private String digitalDeviceUdid;
    private Integer digitalCustomerDeviceId;
    private String deviceName;
    private String deviceType;
    private String deviceOsVersion;
}