package com.unisys.udb.user.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDataForRegisterDevice {
    private UUID digitalCustomerProfileId;
    private int deviceId;
    private String deviceName;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private boolean registeredFlag;
    private String digitalDeviceUUId;
}