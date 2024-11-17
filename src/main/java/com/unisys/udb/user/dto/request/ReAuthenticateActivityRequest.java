package com.unisys.udb.user.dto.request;

import lombok.Data;

@Data
public class ReAuthenticateActivityRequest {
    private String status;
    private String channel;
    private String digitalDeviceUdid;
}
