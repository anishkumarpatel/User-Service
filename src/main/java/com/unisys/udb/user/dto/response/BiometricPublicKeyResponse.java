package com.unisys.udb.user.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@Setter
@Builder
public class BiometricPublicKeyResponse {
    private String status;
    private String publicKey;
    private HttpStatus httpStatus;
    private Date timeStamp;

}
