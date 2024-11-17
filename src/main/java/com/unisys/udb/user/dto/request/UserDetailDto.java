package com.unisys.udb.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDto {
    private String userName;
    private String userMessage;
    private String digitalCustomerDeviceId;
}