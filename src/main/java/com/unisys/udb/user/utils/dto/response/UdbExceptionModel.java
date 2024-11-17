package com.unisys.udb.user.utils.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UdbExceptionModel {
    private String errorMessage;
    private String errorCode;
    private HttpStatus httpStatus;
    private Date timeStamp;

}
