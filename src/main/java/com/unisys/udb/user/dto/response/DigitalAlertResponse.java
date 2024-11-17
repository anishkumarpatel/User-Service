package com.unisys.udb.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DigitalAlertResponse {
    private String digitalAlertKey;
    private boolean alertReadFlag;
    private String alertCreationDate;
    private List<String> params;
}
