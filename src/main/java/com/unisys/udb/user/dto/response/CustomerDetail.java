package com.unisys.udb.user.dto.response;

import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDetail {
    private String name;
    private String username;
    private UUID digitalCustomerProfileId;
    private String deviceName;
    private Timestamp registrationDate;
    private String accessStatus;
    private String phoneNumber;
}
