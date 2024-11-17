package com.unisys.udb.user.exception;

import java.util.UUID;

public class UserStatusException extends RuntimeException {

    public UserStatusException(String message, UUID digitalCustomerProfile) {
        super(message + digitalCustomerProfile);
    }

}
