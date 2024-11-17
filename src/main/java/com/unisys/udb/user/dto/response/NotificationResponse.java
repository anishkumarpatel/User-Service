package com.unisys.udb.user.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationResponse {

    private String response;
    private Integer httpStatusCode;
}