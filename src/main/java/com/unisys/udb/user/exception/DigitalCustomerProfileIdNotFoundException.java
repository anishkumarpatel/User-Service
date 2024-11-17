package com.unisys.udb.user.exception;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;


@RequiredArgsConstructor
@Getter
@Data
public class DigitalCustomerProfileIdNotFoundException extends RuntimeException {
    private final List<String> errorCode;
    private final HttpStatus httpStatus;
    private final String responseType;
    private final String errorMessage;
    private  final List<String> params;

}
