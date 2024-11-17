package com.unisys.udb.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PinChangeResponse {
    private int statusCode;
    private String message;

    public PinChangeResponse(int value, String string) {
        //needed in the flow
    }

    public PinChangeResponse() {
    }
}
