package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.dto.request.SessionHistoryFilterRequest;
import com.unisys.udb.user.dto.request.UserStatusServiceRequest;
import com.unisys.udb.user.dto.response.CustomerSessionHistoryResponse;
import com.unisys.udb.user.entity.CustomerSessionHistory;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import com.unisys.udb.user.entity.DigitalCustomerStatusTypeRef;
import com.unisys.udb.user.exception.DigitalCustomerProfileIdNotFoundException;
import com.unisys.udb.user.exception.NotificationFailure;
import com.unisys.udb.user.exception.UserStatusException;
import com.unisys.udb.user.exception.StatusUpdateReasonNotFoundException;
import com.unisys.udb.user.repository.DigitalCustomerProfileRepository;
import com.unisys.udb.user.repository.DigitalCustomerStatusTypeRefRepository;
import com.unisys.udb.user.repository.InternalReasonRefRepository;
import com.unisys.udb.user.service.DigitalCustomerShortcutsService;
import com.unisys.udb.user.utils.dto.response.NotificationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import static com.unisys.udb.user.constants.UdbConstants.LOCKED;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CAHServiceImplTest {
    @Mock
    private DigitalCustomerProfileRepository digitalCustomerProfileRepository;
    @Mock
    private DigitalCustomerStatusTypeRefRepository digitalCustomerStatusTypeRefRepository;
    @Mock
    private DigitalCustomerShortcutsService digitalCustomerShortcutsService;
    @Mock
    private InternalReasonRefRepository internalReasonRefRepository;
    @InjectMocks
    private CAHServiceImpl cahServiceImpl;

    @Mock
    private NotificationUtil notificationUtil;
    private final Integer three = 3;
    private final Integer four = 4;
    private final Integer one = 1;
    private final Integer two = 2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUnsuspendSuccess() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserStatusServiceRequest request = new UserStatusServiceRequest(
                digitalCustomerProfileId, "Customer request");
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalCustomerProfileId(digitalCustomerProfileId);
        profile.setDigitalCustomerStatusTypeId(three);
        cahServiceImpl.unSuspendDigitalBankingAccess(request, profile, one);
        verify(digitalCustomerProfileRepository).updateDigitalCustomerStatus(Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any());
    }
    @Test
    void testUnsuspendFailure() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserStatusServiceRequest request = new UserStatusServiceRequest(
                digitalCustomerProfileId, "Customer request");
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalCustomerProfileId(digitalCustomerProfileId);
        profile.setDigitalCustomerStatusTypeId(two);
        assertThrows(UserStatusException.class, () -> cahServiceImpl.unSuspendDigitalBankingAccess(request,
                profile, one));
    }
    @Test
    void testSuspendSuccess() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserStatusServiceRequest request = new UserStatusServiceRequest(
                digitalCustomerProfileId, "Customer request");
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalCustomerProfileId(digitalCustomerProfileId);
        profile.setDigitalCustomerStatusTypeId(one);
        cahServiceImpl.suspendDigitalBankingAccess(request, profile, three);
        verify(digitalCustomerProfileRepository).updateDigitalCustomerStatus(Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any());
    }
    @Test
    void testSuspendFailure() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserStatusServiceRequest request = new UserStatusServiceRequest(
                digitalCustomerProfileId, "Customer request");
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalCustomerProfileId(digitalCustomerProfileId);
        profile.setDigitalCustomerStatusTypeId(two);
        assertThrows(UserStatusException.class, () -> cahServiceImpl.suspendDigitalBankingAccess(request,
                profile, three));
    }
    @Test
    void testSuspendFailure1() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserStatusServiceRequest request = new UserStatusServiceRequest(
                digitalCustomerProfileId, "Customer request");
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalCustomerProfileId(digitalCustomerProfileId);
        profile.setDigitalCustomerStatusTypeId(four);
        assertThrows(UserStatusException.class, () -> cahServiceImpl.suspendDigitalBankingAccess(request,
                profile, three));
    }
    @Test
    void testDeleteSuccess() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserStatusServiceRequest request = new UserStatusServiceRequest(
                digitalCustomerProfileId, "Customer request");
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalCustomerProfileId(digitalCustomerProfileId);
        profile.setDigitalCustomerStatusTypeId(one);
        cahServiceImpl.deactivateDigitalBankingAccess(request, profile, four);
        verify(digitalCustomerProfileRepository).updateDigitalCustomerStatus(Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any());
    }
    @Test
    void testDeleteFailure() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserStatusServiceRequest request = new UserStatusServiceRequest(
                digitalCustomerProfileId, "Customer request");
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalCustomerProfileId(digitalCustomerProfileId);
        profile.setDigitalCustomerStatusTypeId(four);
        assertThrows(UserStatusException.class, () -> cahServiceImpl.deactivateDigitalBankingAccess(request,
                profile, four));
    }

    @Test
    void testUnlockNotificationFailure() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserStatusServiceRequest request = new UserStatusServiceRequest(
                digitalCustomerProfileId, "Customer request");
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalCustomerProfileId(digitalCustomerProfileId);
        profile.setDigitalCustomerStatusTypeId(LOCKED);

        // Mock the notificationUtil to throw an exception when sendNotification is called
        doThrow(new NotificationFailure("User unlocked successfully but failed to send notification"))
                .when(notificationUtil).sendNotification(anyMap(), anyMap());

        // Act & Assert
        NotificationFailure exception = assertThrows(NotificationFailure.class, () -> {
            cahServiceImpl.unlockDigitalBankingAccess(request, profile, 1);
        });

        // Verify that the exception message is correct
        assertEquals("User unlocked successfully but failed to send notification", exception.getMessage());

        // Verify that the digitalCustomerProfileRepository.updateDigitalCustomerStatus method was called
        verify(digitalCustomerProfileRepository).updateDigitalCustomerStatus(Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void testUnlockFailure() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserStatusServiceRequest request = new UserStatusServiceRequest(
                digitalCustomerProfileId, "Customer request");
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalCustomerProfileId(digitalCustomerProfileId);
        profile.setDigitalCustomerStatusTypeId(one);
        assertThrows(UserStatusException.class, () -> cahServiceImpl.unlockDigitalBankingAccess(request,
                profile, one));
    }
    @Test
    void testGetCustomerStatus() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalCustomerProfileId(digitalCustomerProfileId);
        profile.setDigitalCustomerStatusTypeId(one);
        when(digitalCustomerProfileRepository.findById(digitalCustomerProfileId)).thenReturn(Optional.of(profile));
        cahServiceImpl.getCustomerStatus(digitalCustomerProfileId);
        verify(digitalCustomerProfileRepository).findById(Mockito.any());
    }
    @Test
    void testGetDigitalCustomerStatusTypeRefId() {
        String status = "Active";
        DigitalCustomerStatusTypeRef digitalCustomerStatusTypeRef = new DigitalCustomerStatusTypeRef();
        digitalCustomerStatusTypeRef.setDigitalCustomerStatusTypeRefId(one);
        when(digitalCustomerStatusTypeRefRepository.findByCustomerStatusTypeIgnoreCase(status))
                .thenReturn(Optional.of(digitalCustomerStatusTypeRef));
        cahServiceImpl.getDigitalCustomerStatusTypeRefId(status);
        verify(digitalCustomerStatusTypeRefRepository).findByCustomerStatusTypeIgnoreCase(Mockito.any());
    }
    @Test
    void testGetCustomerActivityHistorySuccess() {
        UUID coreCustomerProfileId = UUID.randomUUID();
        SessionHistoryFilterRequest request = getActivityHistoryFilterRequest();
        UUID digitalCustomerProfileId = UUID.randomUUID();
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalCustomerProfileId(digitalCustomerProfileId);
        profile.setDigitalCustomerStatusTypeId(one);
        when(digitalCustomerProfileRepository.findByCoreCustomerProfileId(coreCustomerProfileId))
                .thenReturn(Optional.of(profile));
        CustomerSessionHistoryResponse customerSessionHistoryResponse = new CustomerSessionHistoryResponse();
        List<CustomerSessionHistory> customerSessionHistoryList = new ArrayList<>();
        CustomerSessionHistory customerSessionHistory = new CustomerSessionHistory();
        customerSessionHistory.setActivityName("logout");
        customerSessionHistory.setActivityDate(new Date().toString());
        customerSessionHistory.setActivityChannel("Mobile");
        customerSessionHistory.setActivityTime("10:11:12");
        customerSessionHistoryList.add(customerSessionHistory);
        customerSessionHistoryResponse.setCustomerSessionHistory(customerSessionHistoryList);
        when(digitalCustomerShortcutsService.getCustomerSessionHistoryResponse(coreCustomerProfileId, 0, 0,
                request)).thenReturn(customerSessionHistoryResponse);
        cahServiceImpl.getCustomerActivityHistory(coreCustomerProfileId, 0, 0, request);
        verify(digitalCustomerProfileRepository).findByCoreCustomerProfileId(coreCustomerProfileId);
    }
    public SessionHistoryFilterRequest getActivityHistoryFilterRequest() {
        SessionHistoryFilterRequest request = new SessionHistoryFilterRequest();
        Map<String, Boolean> mapChannel = new HashMap<>();
        mapChannel.put("Mobile", true);
        Map<String, Boolean> mapActivity = new HashMap<>();
        mapActivity.put("logout", true);
        Map<String, String> mapDate = new HashMap<>();
        mapDate.put("from", "30-11-2023");
        mapDate.put("to", "30-12-2023");
        request.setByChannel(mapChannel);
        request.setByActivity(mapActivity);
        request.setByDate(mapDate);
        return request;
    }
    @Test
    void testGetCustomerActivityHistoryFailed() {
        UUID coreCustomerProfileId = UUID.randomUUID();
        SessionHistoryFilterRequest request = getActivityHistoryFilterRequest();
        when(digitalCustomerProfileRepository.findByCoreCustomerProfileId(coreCustomerProfileId))
                .thenReturn(Optional.empty());
        assertThrows(DigitalCustomerProfileIdNotFoundException.class, () ->
                cahServiceImpl.getCustomerActivityHistory(coreCustomerProfileId, 0, 0, request));
        verify(digitalCustomerProfileRepository).findByCoreCustomerProfileId(coreCustomerProfileId);
    }
    @Test
    void testGetReasonsForCustomerStatusUpdateSuccess() {
        String status = "Suspend";
        List<String> reasons = new ArrayList<>();
        reasons.add("KYC non compliance");
        when(internalReasonRefRepository.findReasonNameByReasonCategoryIgnoreCase(status))
                .thenReturn(reasons);
        cahServiceImpl.getReasonsForCustomerStatusUpdate(status);
        verify(internalReasonRefRepository).findReasonNameByReasonCategoryIgnoreCase(status);
    }
    @Test
    void testGetReasonsForCustomerStatusUpdateFailure() {
        String status = "Suspend";
        List<String> reasons = new ArrayList<>();
        when(internalReasonRefRepository.findReasonNameByReasonCategoryIgnoreCase(status))
                .thenReturn(reasons);
        assertThrows(StatusUpdateReasonNotFoundException.class,
                () -> cahServiceImpl.getReasonsForCustomerStatusUpdate(status));
    }
}