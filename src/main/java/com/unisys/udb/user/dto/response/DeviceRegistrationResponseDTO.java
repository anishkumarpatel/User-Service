package com.unisys.udb.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class DeviceRegistrationResponseDTO {
    private Boolean isRegistered;
    private String email;
    private String mobileNumber;
}
