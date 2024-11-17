package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.request.DigitalCustomerShortcutsRequest;
import com.unisys.udb.user.dto.request.SessionHistoryFilterRequest;
import com.unisys.udb.user.dto.response.CustomerSessionHistoryResponse;
import com.unisys.udb.user.dto.response.DigitalCustomerShortcutsResponse;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DigitalCustomerShortcutsService {

    Mono<UserAPIBaseResponse> updateDigitalCustomerShortcut(
            UUID digitalCustomerProfileId,
            DigitalCustomerShortcutsRequest digitalCustomerShortcutsRequest);

    Mono<DigitalCustomerShortcutsResponse> getDigitalCustomerShortcut(UUID digitalCustomerProfileId);

    CustomerSessionHistoryResponse getCustomerSessionHistoryResponse(UUID digitalCustomerProfileId, Integer offset,
                                                                     Integer rowCount,
                                                                     SessionHistoryFilterRequest
                                                                             sessionHistoryFilterRequest);
}