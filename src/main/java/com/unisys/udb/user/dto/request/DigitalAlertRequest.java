package com.unisys.udb.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class DigitalAlertRequest {
    @NotNull(message = "Digital customer profile ID cannot be null")
    private UUID digitalCustomerProfileId;
    @NotEmpty(message = "Alert key cannot be null or empty")
    private String alertKey;
}