package com.unisys.udb.user.dto.request;


import com.unisys.udb.user.constants.UdbConstants;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DigitalCustomerProfileDTO {
    private String digitalCustomerProfileId;
    private String digitalUserName;
    private String coreCustomerProfileId;
    private String digitalDeviceUdid;
    private String deviceName;
    private String deviceType;
    private String deviceOsVersion;
    @NotEmpty(message = UdbConstants.PUBLIC_KEY_MISSING_IN_PAYLOAD)
    private String devicePublicKeyForPin;
}
