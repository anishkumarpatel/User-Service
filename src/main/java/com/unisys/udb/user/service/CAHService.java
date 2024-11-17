package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.request.SessionHistoryFilterRequest;
import com.unisys.udb.user.dto.request.UserStatusServiceRequest;
import com.unisys.udb.user.dto.response.CustomerSessionHistoryResponse;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.dto.response.CustomerStatusUpdateReasonResponse;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CAHService {

    Mono<UserAPIBaseResponse> unSuspendDigitalBankingAccess(UserStatusServiceRequest userStatusServiceRequest,
                                                            DigitalCustomerProfile digitalCustomerProfile,
                                                            Integer statusTypeRefId);

    Mono<UserAPIBaseResponse> suspendDigitalBankingAccess(UserStatusServiceRequest userStatusServiceRequest,
                                                          DigitalCustomerProfile digitalCustomerProfile,
                                                          Integer statusTypeRefId);

    Mono<UserAPIBaseResponse> deactivateDigitalBankingAccess(UserStatusServiceRequest userStatusServiceRequest,
                                                         DigitalCustomerProfile digitalCustomerProfile,
                                                         Integer statusTypeRefId);

    Mono<UserAPIBaseResponse> unlockDigitalBankingAccess(UserStatusServiceRequest userStatusServiceRequest,
                                                         DigitalCustomerProfile digitalCustomerProfile,
                                                         Integer statusTypeRefId);

    DigitalCustomerProfile getCustomerStatus(UUID digitalCustomerProfileId);
    Integer getDigitalCustomerStatusTypeRefId(String customerStatusType);
    CustomerSessionHistoryResponse getCustomerActivityHistory(UUID coreCustomerProfileId, Integer offset,
                                                              Integer rowCount,
                                                              SessionHistoryFilterRequest sessionHistoryFilterRequest);
    CustomerStatusUpdateReasonResponse getReasonsForCustomerStatusUpdate(String status);
}
