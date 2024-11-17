package com.unisys.udb.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PublicKeyUpdateRequest {
    @NotNull(message = "digital customer profile Id cannot be null")
    private UUID digitalCustomerProfile;

    @NotBlank(message = "Public key cannot be null")
    private String devicePublicKey;

    @NotNull(message = "Device Id cannot be null")
    private String deviceUdid;
}
