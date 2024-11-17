package com.unisys.udb.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserIdNotFoundException extends RuntimeException {

    public UserIdNotFoundException(Integer userId) {
        super("User not found with ID: " + userId);
    }
}

