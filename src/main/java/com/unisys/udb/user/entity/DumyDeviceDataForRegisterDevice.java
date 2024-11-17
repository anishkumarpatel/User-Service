package com.unisys.udb.user.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DumyDeviceDataForRegisterDevice {
    private int deviceId;
    private String deviceName;
    private String deviceRegisterDate;
    private boolean status;
    private String devicePriority;
    private String deviceDeRegisterDate;
}
