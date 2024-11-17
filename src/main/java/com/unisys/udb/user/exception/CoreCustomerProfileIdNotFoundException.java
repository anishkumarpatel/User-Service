package com.unisys.udb.user.exception;

import java.util.UUID;

public class CoreCustomerProfileIdNotFoundException extends RuntimeException {

    public CoreCustomerProfileIdNotFoundException(UUID digitalCustomerProfileId) {
        super(" Associated CoreCustomerProfileId not exist for digitalCustomerProfileId :  "
                + digitalCustomerProfileId);

    }
}
