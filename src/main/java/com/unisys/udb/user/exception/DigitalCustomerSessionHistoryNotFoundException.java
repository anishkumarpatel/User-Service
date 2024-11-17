package com.unisys.udb.user.exception;

import java.util.UUID;

public class DigitalCustomerSessionHistoryNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DigitalCustomerSessionHistoryNotFoundException(UUID digitalCustomerProfileId) {

        super("No session history found for the digitalCustomerProfileId " + digitalCustomerProfileId);
    }
}