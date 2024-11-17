package com.unisys.udb.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FundTransferValuesNotFound extends RuntimeException {

    public FundTransferValuesNotFound(String message) {
        super(message);
    }
}