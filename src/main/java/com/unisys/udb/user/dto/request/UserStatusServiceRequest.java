package com.unisys.udb.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusServiceRequest {

    @NotNull(message = "DigitalCustomerProfileId required")
    private UUID digitalCustomerProfileId;
    @NotBlank(message = "Reason required")
    private String reason;
}