package com.unisys.udb.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CoreCustomerProfileResponse {

    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;

}
