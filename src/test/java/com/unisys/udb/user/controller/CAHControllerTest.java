package com.unisys.udb.user.controller;


import com.unisys.udb.user.dto.request.SessionHistoryFilterRequest;
import com.unisys.udb.user.dto.request.UserStatusServiceRequest;
import com.unisys.udb.user.dto.response.CustomerSessionHistoryResponse;
import com.unisys.udb.user.dto.response.CustomerStatusUpdateReasonResponse;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.entity.CustomerSessionHistory;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import com.unisys.udb.user.repository.DigitalCustomerProfileRepository;
import com.unisys.udb.user.service.CAHService;
import com.unisys.udb.utility.auditing.dto.BankAuditHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

class CAHControllerTest {

    private final Integer year1970 = 1970;
    private final Integer three = 3;
    private final Integer four = 4;
    private final Integer one = 1;
    private final Integer two = 2;
    @Mock
    private CAHService cahService;
    @Mock
    private BankAuditHolder bankAuditHolder;
    @Mock
    private DigitalCustomerProfileRepository digitalCustomerProfileRepository;
    @InjectMocks
    private CAHController cahController;
    private UserAPIBaseResponse userAPIBaseResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUnSuspendDigitalBankingAccess() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserStatusServiceRequest request = new UserStatusServiceRequest(digitalCustomerProfileId, "Customer request");
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalCustomerProfileId(digitalCustomerProfileId);
        profile.setDigitalCustomerStatusTypeId(three);
        when(cahService.getCustomerStatus(digitalCustomerProfileId)).thenReturn(profile);
        when(cahService.getDigitalCustomerStatusTypeRefId(Mockito.any())).thenReturn(one);
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .status("Success")
                .httpStatus(OK)
                .build();
        when(cahService.unSuspendDigitalBankingAccess(request, profile, one)).thenReturn(Mono.just(response));
        cahController.unSuspendDigitalBankingAccess(request);
        verify(cahService).getCustomerStatus(digitalCustomerProfileId);
    }
    @Test
    void testSuspendDigitalBankingAccess() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserStatusServiceRequest request = new UserStatusServiceRequest(digitalCustomerProfileId, "Customer request");
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalCustomerProfileId(digitalCustomerProfileId);
        profile.setDigitalCustomerStatusTypeId(one);
        when(cahService.getCustomerStatus(digitalCustomerProfileId)).thenReturn(profile);
        when(cahService.getDigitalCustomerStatusTypeRefId(Mockito.any())).thenReturn(three);
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .status("Success")
                .httpStatus(OK)
                .build();
        when(cahService.suspendDigitalBankingAccess(request, profile, three)).thenReturn(Mono.just(response));
        cahController.suspendDigitalBankingAccess(request);
        verify(cahService).getCustomerStatus(digitalCustomerProfileId);
    }
    @Test
    void testDeleteDigitalBankingAccess() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserStatusServiceRequest request = new UserStatusServiceRequest(digitalCustomerProfileId, "Customer request");
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalCustomerProfileId(digitalCustomerProfileId);
        profile.setDigitalCustomerStatusTypeId(one);
        when(cahService.getCustomerStatus(digitalCustomerProfileId)).thenReturn(profile);
        when(cahService.getDigitalCustomerStatusTypeRefId(Mockito.any())).thenReturn(four);
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .status("Success")
                .httpStatus(OK)
                .build();
        when(cahService.deactivateDigitalBankingAccess(request, profile, four)).thenReturn(Mono.just(response));
        cahController.deactivateDigitalBankingAccess(request);
        verify(cahService).getDigitalCustomerStatusTypeRefId(Mockito.any());
    }
    @Test
    void testUnlockDigitalBankingAccess() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserStatusServiceRequest request = new UserStatusServiceRequest(digitalCustomerProfileId, "Customer request");
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalCustomerProfileId(digitalCustomerProfileId);
        profile.setDigitalCustomerStatusTypeId(two);
        when(cahService.getCustomerStatus(digitalCustomerProfileId)).thenReturn(profile);
        when(cahService.getDigitalCustomerStatusTypeRefId(Mockito.any())).thenReturn(one);
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .status("Success")
                .httpStatus(OK)
                .build();
        when(cahService.unlockDigitalBankingAccess(request, profile, one)).thenReturn(Mono.just(response));
        cahController.unlockDigitalBankingAccess(request);
        verify(cahService).getDigitalCustomerStatusTypeRefId(Mockito.any());
    }
    @Test
    void testGetCustomerActivityHistorySuccess() {
        UUID coreCustomerProfileId = UUID.randomUUID();
        CustomerSessionHistoryResponse customerSessionHistoryResponse = new CustomerSessionHistoryResponse();
        List<CustomerSessionHistory> customerSessionHistoryList = new ArrayList<>();
        CustomerSessionHistory customerSessionHistory = new CustomerSessionHistory();
        customerSessionHistory.setActivityName("login");
        customerSessionHistory.setActivityDate(new Date().toString());
        customerSessionHistory.setActivityChannel("Web");
        customerSessionHistory.setActivityTime("10:11:12");
        customerSessionHistoryList.add(customerSessionHistory);
        customerSessionHistoryResponse.setCustomerSessionHistory(customerSessionHistoryList);
        when(cahService.getCustomerActivityHistory(coreCustomerProfileId, 0, 0,
                getSessionHistoryFilterRequest()))
                .thenReturn(customerSessionHistoryResponse);
        cahController.getCustomerActivityHistory(coreCustomerProfileId, 0, 0,
                getSessionHistoryFilterRequest());
        verify(cahService).getCustomerActivityHistory(coreCustomerProfileId, 0, 0,
                getSessionHistoryFilterRequest());
    }
    public SessionHistoryFilterRequest getSessionHistoryFilterRequest() {
        SessionHistoryFilterRequest request = new SessionHistoryFilterRequest();
        Map<String, Boolean> mapChannel = new HashMap<>();
        mapChannel.put("Web", true);
        Map<String, Boolean> mapActivity = new HashMap<>();
        mapActivity.put("login", true);
        Map<String, String> mapDate = new HashMap<>();
        mapDate.put("from", "30-12-2023");
        request.setByChannel(mapChannel);
        request.setByActivity(mapActivity);
        request.setByDate(mapDate);
        return request;
    }
    @Test
    void testGetCustomerActivityHistoryFailure() {
        UUID coreCustomerProfileId = UUID.randomUUID();
        CustomerSessionHistoryResponse customerSessionHistoryResponse = new CustomerSessionHistoryResponse();
        List<CustomerSessionHistory> customerSessionHistoryList = new ArrayList<>();
        customerSessionHistoryResponse.setCustomerSessionHistory(customerSessionHistoryList);
        when(cahService.getCustomerActivityHistory(coreCustomerProfileId, 0, 0,
                getSessionHistoryFilterRequest()))
                .thenReturn(customerSessionHistoryResponse);
        cahController.getCustomerActivityHistory(coreCustomerProfileId, 0, 0,
                getSessionHistoryFilterRequest());
        verify(cahService).getCustomerActivityHistory(coreCustomerProfileId, 0, 0,
                getSessionHistoryFilterRequest());
    }
    @Test
    void testGetReasonForCustomerAccountStatusUpdate() {
        String status = "suspend";
        CustomerStatusUpdateReasonResponse response = CustomerStatusUpdateReasonResponse.builder().build();
        when(cahService.getReasonsForCustomerStatusUpdate(status)).thenReturn(response);
        cahController.getReasonForCustomerAccountStatusUpdate(status);
        verify(cahService).getReasonsForCustomerStatusUpdate(status);
    }
}