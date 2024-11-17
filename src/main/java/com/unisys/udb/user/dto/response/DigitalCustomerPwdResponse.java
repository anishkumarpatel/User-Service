package com.unisys.udb.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DigitalCustomerPwdResponse {
    private UUID digitalProfileId;
    private String message;
}
