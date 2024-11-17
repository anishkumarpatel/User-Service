package com.unisys.udb.user.exception;

import java.util.UUID;

public class DigitalCustomerProfileAlreadyExistsException extends RuntimeException {
    public DigitalCustomerProfileAlreadyExistsException(UUID digitalCustomerProfileId, String msg) {
        super(digitalCustomerProfileId + "already exist in " + msg);
    }
}
