package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.request.DigitalAlertRequest;
import com.unisys.udb.user.dto.response.DigitalAlertResponse;
import com.unisys.udb.user.dto.response.UserSuccessResponse;

import java.util.List;
import java.util.UUID;

public interface DigitalCustomerAlertService {
    List<DigitalAlertResponse> getDigitalCustomerAlerts(UUID digitalCustomerProfileId);

    Integer countUnreadUserAlerts(UUID digitalCustomerProfileId);

    UserSuccessResponse markAlertAsRead(DigitalAlertRequest alertRequest);

    void saveDigitalCustomerAlert(DigitalAlertRequest alertRequest);
}
