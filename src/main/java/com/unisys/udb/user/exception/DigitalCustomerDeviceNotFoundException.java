package com.unisys.udb.user.exception;

import java.util.UUID;

public class DigitalCustomerDeviceNotFoundException extends RuntimeException {
    public DigitalCustomerDeviceNotFoundException(UUID digitalCustomerProfileId) {
        super(" No active DigitalCustomerDevice for digitalCustomerProfileId: " + digitalCustomerProfileId);
    }

    public DigitalCustomerDeviceNotFoundException(String message) {
        super(message);
    }

}
