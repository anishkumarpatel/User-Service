package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.response.CustomerInactivityPeriodResponse;
import com.unisys.udb.user.dto.response.UserActivityStatusResponse;

import java.util.UUID;

public interface DigitalCustomerActivityService {

   CustomerInactivityPeriodResponse checkCustomerInactivityPeriod(UUID digitalCustomerProfileId);

   UserActivityStatusResponse getUserRecentReAuthenticationActivityStatus(UUID digitalCustomerProfileId);

}
