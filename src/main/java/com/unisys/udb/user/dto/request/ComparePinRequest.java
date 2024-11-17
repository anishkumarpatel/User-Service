package com.unisys.udb.user.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComparePinRequest {
    private UUID digitalCustomerProfileId;
    private String newPin;
}