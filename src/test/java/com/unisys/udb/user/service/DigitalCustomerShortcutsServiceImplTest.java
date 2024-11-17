package com.unisys.udb.user.service;


import com.unisys.udb.user.dto.request.DigitalCustomerShortcutsRequest;
import com.unisys.udb.user.dto.request.SessionHistoryFilterRequest;
import com.unisys.udb.user.dto.response.CustomerSessionHistoryResponse;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.dto.response.DigitalCustomerShortcutsResponse;
import com.unisys.udb.user.entity.DigitalCustomerShortcuts;
import com.unisys.udb.user.exception.DigitalCustomerProfileIdNotFoundException;
import com.unisys.udb.user.exception.DigitalCustomerSessionHistoryNotFoundException;
import com.unisys.udb.user.exception.InvalidArgumentException;
import com.unisys.udb.user.repository.DigitalCustomerShortcutsRepository;
import com.unisys.udb.user.repository.PinHistoryRepository;
import com.unisys.udb.user.repository.SesionHistoryRepository;
import com.unisys.udb.user.repository.UserInfoRepository;
import com.unisys.udb.user.service.impl.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.*;

import java.time.LocalDateTime;
import java.util.*;
import java.sql.Date;
import java.util.function.Consumer;

import static com.unisys.udb.user.constants.UdbConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {DigitalCustomerShortcutsServiceImpl.class})
@ExtendWith(SpringExtension.class)
class DigitalCustomerShortcutsServiceImplTest {
    private final int year = 1970;
    private final int ten = 10;
    @MockBean
    private DigitalCustomerShortcutsRepository digitalCustomerShortcutsRepository;

    @MockBean
    private SesionHistoryRepository sesionHistoryRepository;
    @MockBean
    private PinHistoryRepository pinHistoryRepository;
    @MockBean
    private UserInfoRepository userInfoRepository;

    @Autowired
    private DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsServiceImpl;

    private UUID digitalCustomerProfileId = UUID.fromString("D5E2AFD4-DB81-4F03-9CBF-0ED7FC8046C6");

    @Test
    void testUpdateDigitalCustomerShortcutSuccess() {
        DigitalCustomerShortcuts digitalCustomerShortcuts = new DigitalCustomerShortcuts();
        digitalCustomerShortcuts.setCmnctnPreferenceShortcut(true);
        digitalCustomerShortcuts.setDigitalCustomerProfileId(UUID.randomUUID());
        digitalCustomerShortcuts.setDigitalCustomerShortcutsId(1L);
        digitalCustomerShortcuts.setEstatementShortcut(true);
        digitalCustomerShortcuts.setFundTransferShortcut(true);
        digitalCustomerShortcuts.setPayeeShortcut(true);
        digitalCustomerShortcuts.setScheduledPaymentsShortcut(true);
        digitalCustomerShortcuts.setSessionHistoryShortcut(true);
        digitalCustomerShortcuts.setShortcutCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerShortcuts
                .setShortcutCreationDate(LocalDateTime.of(year, 1, 1, 0, 0));
        digitalCustomerShortcuts.setShortcutModificationDate(
                LocalDateTime.of(year, 1, 1, 0, 0));
        digitalCustomerShortcuts.setShortcutModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        Optional<DigitalCustomerShortcuts> ofResult = Optional.of(digitalCustomerShortcuts);

        DigitalCustomerShortcuts digitalCustomerShortcuts2 = new DigitalCustomerShortcuts();
        digitalCustomerShortcuts2.setCmnctnPreferenceShortcut(true);
        digitalCustomerShortcuts2.setDigitalCustomerProfileId(UUID.randomUUID());
        digitalCustomerShortcuts2.setDigitalCustomerShortcutsId(1L);
        digitalCustomerShortcuts2.setEstatementShortcut(true);
        digitalCustomerShortcuts2.setFundTransferShortcut(true);
        digitalCustomerShortcuts2.setPayeeShortcut(true);
        digitalCustomerShortcuts2.setScheduledPaymentsShortcut(true);
        digitalCustomerShortcuts2.setSessionHistoryShortcut(true);
        digitalCustomerShortcuts2.setShortcutCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerShortcuts2
                .setShortcutCreationDate(LocalDateTime.of(year, 1, 1, 0, 0));
        digitalCustomerShortcuts2.setShortcutModificationDate(
                LocalDateTime.of(year, 1, 1, 0, 0));
        digitalCustomerShortcuts2.setShortcutModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        Mockito.when(digitalCustomerShortcutsRepository.save(Mockito.<DigitalCustomerShortcuts>any()))
                .thenReturn(digitalCustomerShortcuts2);
        Mockito.when(digitalCustomerShortcutsRepository.findByDigitalCustomerProfileId(
                Mockito.<UUID>any())).thenReturn(ofResult);
        UUID digitalCustomerProfileId = UUID.randomUUID();

        final Mono<UserAPIBaseResponse> userAPIBaseResponseMono = digitalCustomerShortcutsServiceImpl.
                updateDigitalCustomerShortcut(digitalCustomerProfileId,
                new DigitalCustomerShortcutsRequest());


        verify(digitalCustomerShortcutsRepository).findByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(digitalCustomerShortcutsRepository).save(Mockito.<DigitalCustomerShortcuts>any());
    }

    @Test
    void testUpdateDigitalCustomerShortcutFalse() {
        DigitalCustomerShortcuts digitalCustomerShortcuts = new DigitalCustomerShortcuts();
        digitalCustomerShortcuts.setCmnctnPreferenceShortcut(false);
        digitalCustomerShortcuts.setDigitalCustomerProfileId(UUID.randomUUID());
        digitalCustomerShortcuts.setDigitalCustomerShortcutsId(1L);
        digitalCustomerShortcuts.setEstatementShortcut(false);
        digitalCustomerShortcuts.setFundTransferShortcut(false);
        digitalCustomerShortcuts.setPayeeShortcut(false);
        digitalCustomerShortcuts.setScheduledPaymentsShortcut(false);
        digitalCustomerShortcuts.setSessionHistoryShortcut(false);
        digitalCustomerShortcuts.setShortcutCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerShortcuts
                .setShortcutCreationDate(LocalDateTime.of(year, 1, 1, 0, 0));
        digitalCustomerShortcuts.setShortcutModificationDate(
                LocalDateTime.of(year, 1, 1, 0, 0));
        digitalCustomerShortcuts.setShortcutModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        Optional<DigitalCustomerShortcuts> ofResult = Optional.of(digitalCustomerShortcuts);

        DigitalCustomerShortcuts digitalCustomerShortcuts2 = new DigitalCustomerShortcuts();
        digitalCustomerShortcuts2.setCmnctnPreferenceShortcut(false);
        digitalCustomerShortcuts2.setDigitalCustomerProfileId(UUID.randomUUID());
        digitalCustomerShortcuts2.setDigitalCustomerShortcutsId(1L);
        digitalCustomerShortcuts2.setEstatementShortcut(false);
        digitalCustomerShortcuts2.setFundTransferShortcut(false);
        digitalCustomerShortcuts2.setPayeeShortcut(false);
        digitalCustomerShortcuts2.setScheduledPaymentsShortcut(false);
        digitalCustomerShortcuts2.setSessionHistoryShortcut(false);
        digitalCustomerShortcuts2.setShortcutCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerShortcuts2
                .setShortcutCreationDate(LocalDateTime.of(year, 1, 1, 0, 0));
        digitalCustomerShortcuts2.setShortcutModificationDate(
                LocalDateTime.of(year, 1, 1, 0, 0));
        digitalCustomerShortcuts2.setShortcutModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        Mockito.when(digitalCustomerShortcutsRepository.save(Mockito.<DigitalCustomerShortcuts>any()))
                .thenReturn(digitalCustomerShortcuts2);
        Mockito.when(digitalCustomerShortcutsRepository.findByDigitalCustomerProfileId(
                Mockito.<UUID>any())).thenReturn(ofResult);
        UUID digitalCustomerProfileId = UUID.randomUUID();

        digitalCustomerShortcutsServiceImpl.updateDigitalCustomerShortcut(digitalCustomerProfileId,
                new DigitalCustomerShortcutsRequest());

        assertFalse(digitalCustomerShortcuts2.getPayeeShortcut());
    }

    @Test
    void testSetShortcutIfNotNullWithValueNotNull() {
        // Arrange
        Boolean value = Boolean.TRUE;
        Consumer<Boolean> setter = mock(Consumer.class);

        // Act
        DigitalCustomerShortcutsServiceImpl.setShortcutIfNotNull(value, setter);

        // Assert
        verify(setter).accept(value);
    }

    @Test
    void emptyExistingShortcut() {
        Optional<DigitalCustomerShortcuts> emptyResult = Optional.empty();
        when(digitalCustomerShortcutsRepository.findByDigitalCustomerProfileId(null)).thenReturn(emptyResult);
        UUID digitalCustomerProfileId = UUID.fromString("2161c98f-113d-4771-afee-5e652c501493");

        try {
            digitalCustomerShortcutsServiceImpl.updateDigitalCustomerShortcut(digitalCustomerProfileId,
                    new DigitalCustomerShortcutsRequest());
        } catch (DigitalCustomerProfileIdNotFoundException ex) {
            List<String> errorCode = new ArrayList<>();
            errorCode.add(NOT_FOUND_ERROR_CODE);
            assertEquals(new DigitalCustomerProfileIdNotFoundException(errorCode,
                    HttpStatus.NOT_FOUND,
                    FAILURE,
                    NOT_FOUND_ERROR_MESSAGE + UUID.randomUUID(),
                    new ArrayList<>()

            ).getHttpStatus(), ex.getHttpStatus());
        }
    }

    @Test
    void testUpdateDigitalCustomerShortcutWithNullRequest() {
        UUID digitalCustomerProfileId = UUID.randomUUID();

        assertThrows(DigitalCustomerProfileIdNotFoundException.class, () -> digitalCustomerShortcutsServiceImpl
                .updateDigitalCustomerShortcut(digitalCustomerProfileId, null));
    }

    @Test
    void testGetDigitalCustomerShortcut() {
        // Arrange
        DigitalCustomerShortcuts digitalCustomerShortcuts = new DigitalCustomerShortcuts();
        digitalCustomerShortcuts.setCmnctnPreferenceShortcut(true);
        digitalCustomerShortcuts.setDigitalCustomerProfileId(UUID.randomUUID());
        digitalCustomerShortcuts.setDigitalCustomerShortcutsId(1L);
        digitalCustomerShortcuts.setEstatementShortcut(true);
        digitalCustomerShortcuts.setFundTransferShortcut(true);
        digitalCustomerShortcuts.setPayeeShortcut(true);
        digitalCustomerShortcuts.setScheduledPaymentsShortcut(true);
        digitalCustomerShortcuts.setSessionHistoryShortcut(true);
        digitalCustomerShortcuts.setShortcutCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerShortcuts
                .setShortcutCreationDate(LocalDateTime.of(year, 1, 1, 0, 0));
        digitalCustomerShortcuts.setShortcutModificationDate(
                LocalDateTime.of(year, 1, 1, 0, 0));
        digitalCustomerShortcuts.setShortcutModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        Optional<DigitalCustomerShortcuts> ofResult = Optional.of(digitalCustomerShortcuts);
        when(digitalCustomerShortcutsRepository
                .findByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn(ofResult);
        when(userInfoRepository.existsByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn(true);
        when(userInfoRepository.findUserNameByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn("janedoe");
        digitalCustomerShortcutsServiceImpl.getDigitalCustomerShortcut(null);
        verify(digitalCustomerShortcutsRepository).findByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(userInfoRepository).existsByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(userInfoRepository).findUserNameByDigitalCustomerProfileId(Mockito.<UUID>any());
    }

    @Test
    void shouldRetrieveDigitalCustomerShortcutsByProfileId() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        LocalDateTime shortcutCreationDate = LocalDateTime.of(year, 1, 1, 0, 0);
        DigitalCustomerShortcuts digitalCustomerShortcuts = new DigitalCustomerShortcuts(
                1L, digitalCustomerProfileId, true,
                true, true, true,
                true, true, shortcutCreationDate,
                "Jan 1, 2020 8:00am GMT+0100",
                LocalDateTime.of(year, 1, 1, 0, 0),
                "Jan 1, 2020 9:00am GMT+0100");
        digitalCustomerShortcuts.setCmnctnPreferenceShortcut(true);
        digitalCustomerShortcuts.setDigitalCustomerProfileId(UUID.randomUUID());
        digitalCustomerShortcuts.setDigitalCustomerShortcutsId(1L);
        digitalCustomerShortcuts.setEstatementShortcut(true);
        digitalCustomerShortcuts.setFundTransferShortcut(true);
        digitalCustomerShortcuts.setPayeeShortcut(true);
        digitalCustomerShortcuts.setScheduledPaymentsShortcut(true);
        digitalCustomerShortcuts.setSessionHistoryShortcut(true);
        digitalCustomerShortcuts.setShortcutCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerShortcuts
                .setShortcutCreationDate(LocalDateTime.of(year, 1, 1, 0, 0));
        digitalCustomerShortcuts.setShortcutModificationDate(
                LocalDateTime.of(year, 1, 1, 0, 0));
        digitalCustomerShortcuts.setShortcutModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        Optional<DigitalCustomerShortcuts> ofResult = Optional.of(digitalCustomerShortcuts);
        when(digitalCustomerShortcutsRepository
                .findByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn(ofResult);
        when(userInfoRepository.existsByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn(true);
        when(userInfoRepository.findUserNameByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn("janedoe");
        digitalCustomerShortcutsServiceImpl.getDigitalCustomerShortcut(null);
        verify(digitalCustomerShortcutsRepository).findByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(userInfoRepository).existsByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(userInfoRepository).findUserNameByDigitalCustomerProfileId(Mockito.<UUID>any());

    }

   @Test
    void shouldPropagateExceptionWhenDigitalCustomerProfileIdNotFound() {
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        // Arrange
        when(userInfoRepository.existsByDigitalCustomerProfileId(Mockito.<UUID>any()))
                .thenThrow(new DigitalCustomerProfileIdNotFoundException(errorCode,
                        HttpStatus.NOT_FOUND,
                        FAILURE,
                        NOT_FOUND_ERROR_MESSAGE + UUID.randomUUID(),
                        new ArrayList<>()));

       // Act and Assert
       final UUID digitalId = Mockito.<UUID>any();

       assertThrows(DigitalCustomerProfileIdNotFoundException.class, () -> {
            digitalCustomerShortcutsServiceImpl.getDigitalCustomerShortcut(digitalId);
        });

    }

    @Test
    void testGetCustomerSessionHistoryResponse() {
        // Arrange
        List<Object[]> sesionHistoryDetailsObjList = new ArrayList<>();
        Object[] customerSessionAttrArray = new Object[FIVE_CONSTANT];
        customerSessionAttrArray[ZERO_CONSTANT] = "login success";
        customerSessionAttrArray[ONE_CONSTANT] = new Date(DATE);
        customerSessionAttrArray[TWO_CONSTANT] = "01:01:10";
        customerSessionAttrArray[THREE_CONSTANT] = "Samsung S23";
        customerSessionAttrArray[FOUR_CONSTANT] = "Mobile";
        sesionHistoryDetailsObjList.add(customerSessionAttrArray);
        when(sesionHistoryRepository.getCustomerSessionHistoryDetails(Mockito.<UUID>any(), Mockito.<Integer>any(),
                Mockito.<Integer>any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(sesionHistoryDetailsObjList);

        CustomerSessionHistoryResponse customerSessionHistoryResponse = digitalCustomerShortcutsServiceImpl
                .getCustomerSessionHistoryResponse(UUID.fromString("6A3D3115-E0D4-4F63-B9BA-80596AE98305"),
                        0, ten, getValidSessionHistoryFilterRequest());
        assertNotNull(customerSessionHistoryResponse);
    }
    @Test
    void testGetCustomerSessionHistoryResponseSuccess() {
        // Arrange
        List<Object[]> sesionHistoryDetailsObjList = new ArrayList<>();
        Object[] customerSessionAttrArray = new Object[FIVE_CONSTANT];
        customerSessionAttrArray[ZERO_CONSTANT] = "login success";
        customerSessionAttrArray[ONE_CONSTANT] = new Date(DATE);
        customerSessionAttrArray[TWO_CONSTANT] = "01:01:10";
        customerSessionAttrArray[THREE_CONSTANT] = "Samsung S23";
        customerSessionAttrArray[FOUR_CONSTANT] = "Mobile";
        sesionHistoryDetailsObjList.add(customerSessionAttrArray);
        when(sesionHistoryRepository.getCustomerSessionHistoryDetails(Mockito.<UUID>any(), Mockito.<Integer>any(),
                Mockito.<Integer>any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(sesionHistoryDetailsObjList);
        CustomerSessionHistoryResponse customerSessionHistoryResponse = digitalCustomerShortcutsServiceImpl
                .getCustomerSessionHistoryResponse(UUID.fromString("6A3D3115-E0D4-4F63-B9BA-80596AE98305"),
                        0, ten, getValidSessionHistoryFilterRequestEmptyMap());
        assertNotNull(customerSessionHistoryResponse);
    }
    @Test
    void testGetCustomerSessionHistoryResponseFailure() {
        List<Object[]> sesionHistoryDetailsObjList = new ArrayList<>();
        when(sesionHistoryRepository.getCustomerSessionHistoryDetails(Mockito.<UUID>any(), Mockito.<Integer>any(),
                Mockito.<Integer>any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(sesionHistoryDetailsObjList);
        SessionHistoryFilterRequest request = getValidSessionHistoryFilterRequest();
        UUID id = UUID.fromString("6A3D3115-E0D4-4F63-B9BA-80596AE98305");
        assertThrows(DigitalCustomerSessionHistoryNotFoundException.class, () -> digitalCustomerShortcutsServiceImpl
                .getCustomerSessionHistoryResponse(id, 0, 0, request));
    }
    @Test
    void testGetCustomerSessionHistoryResponseFailure1() {
        when(sesionHistoryRepository.getCustomerSessionHistoryDetails(Mockito.<UUID>any(), Mockito.<Integer>any(),
                Mockito.<Integer>any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(null);
        SessionHistoryFilterRequest request = getValidSessionHistoryFilterRequest();
        UUID id = UUID.fromString("6A3D3115-E0D4-4F63-B9BA-80596AE98305");
        assertThrows(DigitalCustomerSessionHistoryNotFoundException.class, () -> digitalCustomerShortcutsServiceImpl
                .getCustomerSessionHistoryResponse(id, 0, 0, request));
    }
    @Test
    void testGetCustomerSessionHistoryResponseInvalidRowCount() {
        SessionHistoryFilterRequest request = getValidSessionHistoryFilterRequest();
        UUID id = UUID.fromString("6A3D3115-E0D4-4F63-B9BA-80596AE98305");
        assertThrows(InvalidArgumentException.class, () -> digitalCustomerShortcutsServiceImpl
                .getCustomerSessionHistoryResponse(id, 0, -1, request));
    }
    @Test
    void testGetCustomerSessionHistoryResponseInvalidOffset() {
        SessionHistoryFilterRequest request = getValidSessionHistoryFilterRequest();
        UUID id = UUID.fromString("6A3D3115-E0D4-4F63-B9BA-80596AE98305");
        assertThrows(InvalidArgumentException.class, () -> digitalCustomerShortcutsServiceImpl
                .getCustomerSessionHistoryResponse(id, -1, 0, request));
    }
    @Test
    void testGetCustomerSessionHistoryResponseInvalidFilterRequest() {
        SessionHistoryFilterRequest request = getInValidSessionHistoryFilterRequest();
        UUID id = UUID.fromString("6A3D3115-E0D4-4F63-B9BA-80596AE98305");
        assertThrows(InvalidArgumentException.class, () -> digitalCustomerShortcutsServiceImpl
                .getCustomerSessionHistoryResponse(id, 0, 0, request));
    }
    @Test
    void testGetCustomerSessionHistoryResponseMissingDateField() {
        SessionHistoryFilterRequest request = getSessionHistoryFilterRequestMissingDateValue();
        UUID id = UUID.fromString("6A3D3115-E0D4-4F63-B9BA-80596AE98305");
        assertThrows(InvalidArgumentException.class, () -> digitalCustomerShortcutsServiceImpl
                .getCustomerSessionHistoryResponse(id, 0, 0, request));
    }
    @Test
    void testGetCustomerSessionHistoryResponseMissingDateValue() {
        SessionHistoryFilterRequest request = getValidSessionHistoryFilterRequestEmptyDate();
        UUID id = UUID.fromString("6A3D3115-E0D4-4F63-B9BA-80596AE98305");
        assertThrows(InvalidArgumentException.class, () -> digitalCustomerShortcutsServiceImpl
                .getCustomerSessionHistoryResponse(id, 0, 0, request));
    }
    @Test
    void testGetCustomerSessionHistoryResponseNullRequest() {
        SessionHistoryFilterRequest request = getSessionHistoryFilterRequestMissingDateValue();
        UUID id = UUID.fromString("6A3D3115-E0D4-4F63-B9BA-80596AE98305");
        assertThrows(InvalidArgumentException.class, () -> digitalCustomerShortcutsServiceImpl
                .getCustomerSessionHistoryResponse(id, 0, 0, null));
    }

    public SessionHistoryFilterRequest getValidSessionHistoryFilterRequest() {
        SessionHistoryFilterRequest request = new SessionHistoryFilterRequest();
        Map<String, Boolean> mapChannel = new HashMap<>();
        mapChannel.put("Web", true);
        Map<String, Boolean> mapActivity = new HashMap<>();
        mapActivity.put("login", true);
        Map<String, String> mapDate = new HashMap<>();
        mapDate.put("from", "30-12-2023");
        mapDate.put("to", "30-12-2024");
        request.setByChannel(mapChannel);
        request.setByActivity(mapActivity);
        request.setByDate(mapDate);
        return request;
    }
    public SessionHistoryFilterRequest getValidSessionHistoryFilterRequestEmptyMap() {
        SessionHistoryFilterRequest request = new SessionHistoryFilterRequest();
        Map<String, Boolean> mapChannel = new HashMap<>();
        Map<String, Boolean> mapActivity = new HashMap<>();
        Map<String, String> mapDate = new HashMap<>();
        mapDate.put("from", "12-03-2023");
        mapDate.put("to", "14-04-2024");
        request.setByChannel(mapChannel);
        request.setByActivity(mapActivity);
        request.setByDate(mapDate);
        return request;
    }
    public SessionHistoryFilterRequest getValidSessionHistoryFilterRequestEmptyDate() {
        SessionHistoryFilterRequest request = new SessionHistoryFilterRequest();
        Map<String, Boolean> mapChannel = new HashMap<>();
        Map<String, Boolean> mapActivity = new HashMap<>();
        Map<String, String> mapDate = new HashMap<>();
        mapDate.put("from", "");
        mapDate.put("to", "14-04-2024");
        request.setByChannel(mapChannel);
        request.setByActivity(mapActivity);
        request.setByDate(mapDate);
        return request;
    }

    public SessionHistoryFilterRequest getInValidSessionHistoryFilterRequest() {
        SessionHistoryFilterRequest request = new SessionHistoryFilterRequest();
        Map<String, Boolean> mapChannel = new HashMap<>();
        mapChannel.put("Web", true);
        Map<String, Boolean> mapActivity = new HashMap<>();
        mapActivity.put("login", true);
        request.setByChannel(mapChannel);
        request.setByActivity(mapActivity);
        return request;
    }
    public SessionHistoryFilterRequest getSessionHistoryFilterRequestMissingDateValue() {
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
    void testGetDigitalCustomerShortcutWhenShortcutsDoNotExist() {
        DigitalCustomerShortcuts digitalCustomerShortcuts;
        DigitalCustomerShortcutsResponse digitalCustomerShortcutsResponse;

        digitalCustomerShortcuts = new DigitalCustomerShortcuts();
        digitalCustomerShortcuts.setDigitalCustomerProfileId(digitalCustomerProfileId);
        digitalCustomerShortcuts.setFundTransferShortcut(true);
        digitalCustomerShortcuts.setPayeeShortcut(true);
        digitalCustomerShortcuts.setEstatementShortcut(true);
        digitalCustomerShortcuts.setSessionHistoryShortcut(false);
        digitalCustomerShortcuts.setScheduledPaymentsShortcut(false);
        digitalCustomerShortcuts.setCmnctnPreferenceShortcut(false);
        digitalCustomerShortcuts.setShortcutModifiedBy("user");
        digitalCustomerShortcuts.setShortcutCreatedBy("user");
        digitalCustomerShortcuts.setShortcutCreationDate(LocalDateTime.now());
        digitalCustomerShortcuts.setShortcutModificationDate(LocalDateTime.now());

        digitalCustomerShortcutsResponse = new DigitalCustomerShortcutsResponse();
        digitalCustomerShortcutsResponse.setPayeeShortcut(true);
        digitalCustomerShortcutsResponse.setEStatementShortcut(true);
        digitalCustomerShortcutsResponse.setFundTransferShortcut(true);
        digitalCustomerShortcutsResponse.setScheduledPaymentsShortcut(false);
        digitalCustomerShortcutsResponse.setCommPrefShortcut(false);
        digitalCustomerShortcutsResponse.setSessionHistoryShortcut(false);
        // Arrange
        when(digitalCustomerShortcutsRepository.findByDigitalCustomerProfileId(digitalCustomerProfileId))
                .thenReturn(Optional.empty());
        when(userInfoRepository.findUserNameByDigitalCustomerProfileId(digitalCustomerProfileId))
                .thenReturn("user");
        when(userInfoRepository.existsByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn(true);
        when(userInfoRepository.findUserNameByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn("janedoe");


        // Act
        Mono<DigitalCustomerShortcutsResponse> result =
                digitalCustomerShortcutsServiceImpl.getDigitalCustomerShortcut(digitalCustomerProfileId);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    assertEquals(true, response.getPayeeShortcut());
                    assertEquals(true, response.getEStatementShortcut());
                    assertEquals(true, response.getFundTransferShortcut());
                    assertEquals(false, response.getScheduledPaymentsShortcut());
                    assertEquals(false, response.getCommPrefShortcut());
                    assertEquals(false, response.getSessionHistoryShortcut());
                    return true;
                })
                .verifyComplete();

        verify(digitalCustomerShortcutsRepository, times(1)).save(any(DigitalCustomerShortcuts.class));
    }
}