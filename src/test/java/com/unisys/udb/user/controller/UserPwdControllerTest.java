package com.unisys.udb.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisys.udb.user.dto.request.DigitalPwdRequest;
import com.unisys.udb.user.dto.response.DigitalCustomerPwdResponse;
import com.unisys.udb.user.dto.response.UserInfoResponse;
import com.unisys.udb.user.entity.DigitalCustomerDevice;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import com.unisys.udb.user.entity.DigitalDeviceLink;
import com.unisys.udb.user.exception.CustomerOldPwdException;
import com.unisys.udb.user.repository.*;
import com.unisys.udb.user.service.*;
import com.unisys.udb.user.service.client.ConfigurationServiceClient;
import com.unisys.udb.user.service.impl.UserInfoServiceImpl;
import com.unisys.udb.user.utils.JsonUtils;
import com.unisys.udb.user.utils.dto.response.NotificationUtil;
import com.unisys.udb.utility.auditing.dto.AuditDigitalCustomerHolder;
import com.unisys.udb.utility.auditing.dto.CustomerActionAuditHolder;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserPwdControllerTest {

    @MockBean
    private PinService pinService;

    @MockBean
    private UserInfoService userInfoService;

    @MockBean
    private UserInfoServiceImpl userInfoServiceImpl;

    @MockBean
    private DigitalCustomerShortcutsService digitalCustomerShortcutsService;

    @MockBean
    private DigitalCookiesPreferenceService digitalCookiesPreferenceService;

    @MockBean
    private AuditDigitalCustomerHolder auditDigitalCustomerIdHolder;

    @MockBean
    private PromotionOffers promotionOffers;

    @MockBean
    private DigitalCustomerDeviceService digitalDeviceService;

    @MockBean
    private UserRegistrationService userRegistrationService;

    @MockBean
    private CustomerActionAuditHolder customerActionAuditHolder;

    @MockBean
    private JsonUtils jsonUtils;


    @Test
    public void saveOldPassword() throws CustomerOldPwdException {
        // Arrange
        UUID digitalProfileId = UUID.randomUUID();
        String password = "myOldPassword";
        DigitalPwdRequest request = DigitalPwdRequest.builder()
                .digitalProfileId(digitalProfileId)
                .password(password)
                .build();

        String expectedMessage = "Old password saved successfully";
        DigitalCustomerPwdResponse expectedResponse = new DigitalCustomerPwdResponse(digitalProfileId, expectedMessage);
        UserInfoRepository customerRepository = mock(UserInfoRepository.class);
        DigitalCookiePreferenceRepository digitalCookiePreferenceRepository = mock(
                DigitalCookiePreferenceRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository =
                mock(DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);

        UserInfoService mockUserInfoService = mock(UserInfoService.class);
        when(mockUserInfoService.storeOldPassword(request)).thenReturn(expectedResponse);
        JsonUtils jsonUtils1 = new JsonUtils(new CustomerActionAuditHolder(), new ObjectMapper());
        UserController userController = new UserController(new UserRegistrationServiceImpl(customerRepository,
                digitalCustomerProfileRepository,
                bankingNotificationPreferenceRepository, marketingNotificationPreferencesRepository,
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl)),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                digitalCookiePreferenceRepository, mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class)),
                mockUserInfoService, pinService,
                digitalCustomerShortcutsService, digitalCookiesPreferenceService,
                auditDigitalCustomerIdHolder, promotionOffers, digitalDeviceService, jsonUtils1);

        DigitalCustomerPwdResponse actualResponse = userController.saveOldPassword(request, "hello");

        assertEquals(expectedResponse.getDigitalProfileId(), actualResponse.getDigitalProfileId());
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    }



    @Test
    public void testGetUserInfo() {
        DigitalCustomerProfile digitalCustomerProfile = new DigitalCustomerProfile();
        digitalCustomerProfile.setCoreCustomerProfileId(UUID.randomUUID());
        digitalCustomerProfile.setProfileCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerProfile.setRegistrationDate(LocalDateTime.now());
        digitalCustomerProfile.setDigitalCustomerProfileId(UUID.randomUUID());
        digitalCustomerProfile.setDigitalCustomerStatusTypeId(1);
        digitalCustomerProfile.setDigitalUserName("janedoe");
        digitalCustomerProfile.setProfileModifiedBy("2020-03-01");
        digitalCustomerProfile.setProfileModificationDate(LocalDateTime.now());

        DigitalCustomerDevice digitalCustomerDevice = new DigitalCustomerDevice();
        digitalCustomerDevice.setDigitalDeviceUdid("835185ffd672ab9c");
        digitalCustomerDevice.setDeviceName("testDeviceName");
        digitalCustomerDevice.setDeviceType("testDeviceType");
        digitalCustomerDevice.setDeviceOsVersion("testOsVersion");
        digitalCustomerDevice.setDeviceToken("testDeviceToken");
        digitalCustomerDevice.setDeviceStatus(true);
        digitalCustomerDevice.setDeviceCreationDate(LocalDateTime.now());
        digitalCustomerDevice.setDeviceCreatedBy("testCreator");
        digitalCustomerDevice.setDeviceModificationDate(LocalDateTime.now());
        digitalCustomerDevice.setDeviceModifiedBy("testModifier");
        digitalCustomerDevice.setTermsAndConditions(true);
        digitalCustomerDevice.setStrictlyAcceptanceCookie(true);
        digitalCustomerDevice.setPerformanceCookie(true);
        digitalCustomerDevice.setFunctionalCookie(true);
        digitalCustomerDevice.setDeviceFacePublicKey("testFacePublicKey");
        digitalCustomerDevice.setDeviceTouchPublicKey("testTouchPublicKey");
        digitalCustomerDevice.setDevicePinPublicKey("testPinPublicKey");

        DigitalDeviceLink digitalDeviceLink = new DigitalDeviceLink();
        digitalDeviceLink.setDigitalCustomerProfile(digitalCustomerProfile);
        digitalDeviceLink.setDigitalCustomerDevice(digitalCustomerDevice);

        List<DigitalDeviceLink> digitalDeviceLinks = new ArrayList<>();
        digitalDeviceLinks.add(digitalDeviceLink);
        digitalCustomerProfile.setDigitalDeviceLink(digitalDeviceLinks);

        Optional<DigitalCustomerProfile> ofResult = Optional.of(digitalCustomerProfile);

        UserInfoRepository customerRepository = mock(UserInfoRepository.class);
        when(customerRepository.existsByDigitalCustomerProfileId(Mockito.any())).thenReturn(true);
        when(customerRepository.findByDigitalCustomerProfileId(Mockito.any())).thenReturn(ofResult);

        DigitalCustomerProfileRepository digitalCustomerProfileRepository =
                mock(DigitalCustomerProfileRepository.class);
        List<String> digitalCustomerDeviceIdList = new ArrayList<>();
        digitalCustomerDeviceIdList.add("835185ffd672ab9c");
        when(digitalCustomerProfileRepository.findDigitalCustomerDeviceIdList(Mockito.any()))
                .thenReturn(digitalCustomerDeviceIdList);

        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository =
                mock(BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository =
                mock(MarketingNotificationPreferencesRepository.class);
        DigitalCookiePreferenceRepository digitalCookiePreferenceRepository =
                mock(DigitalCookiePreferenceRepository.class);
        DigitalCustomerDeviceRepository digitalCustomerDeviceRepository = mock(DigitalCustomerDeviceRepository.class);

        UserController userController = new UserController(
                new UserRegistrationServiceImpl(customerRepository, digitalCustomerProfileRepository,
                        bankingNotificationPreferenceRepository, marketingNotificationPreferencesRepository,
                        new DigitalCustomerProfileAndDeviceInjector(
                                mock(String.valueOf(userInfoServiceImpl)),
                                mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                                mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                                mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                                mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                                mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                        digitalCookiePreferenceRepository, digitalCustomerDeviceRepository,
                        mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                        mock(NotificationUtil.class)),
                userInfoService, pinService, digitalCustomerShortcutsService, digitalCookiesPreferenceService,
                auditDigitalCustomerIdHolder, promotionOffers, digitalDeviceService,
                jsonUtils);

        // Act
        userController.getUserInfo(UUID.randomUUID(), "835185ffd672ab9c");

        verify(customerRepository).existsByDigitalCustomerProfileId(Mockito.any());
        verify(customerRepository).findByDigitalCustomerProfileId(Mockito.any());
        verify(digitalCustomerProfileRepository).findDigitalCustomerDeviceIdList(Mockito.any());
    }


    @Test
    public void testGetUserInfo2() {
        UserRegistrationServiceImpl userRegistrationServiceMock = mock(UserRegistrationServiceImpl.class);

        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setDigitalUserName("janedoe");
        userInfoResponse.setCoreCustomerProfileId(UUID.randomUUID());
        userInfoResponse.setDigitalCustomerStatusTypeId(1);
        userInfoResponse.setDigitalCustomerDeviceId("835185ffd672ab9c");

        Mono<UserInfoResponse> justResult = Mono.just(userInfoResponse);
        when(userRegistrationServiceMock.getUserInfo(Mockito.any(), Mockito.eq("835185ffd672ab9c")))
                .thenReturn(justResult);
        UserController userController = new UserController(userRegistrationServiceMock, userInfoService, pinService,
                digitalCustomerShortcutsService, digitalCookiesPreferenceService, auditDigitalCustomerIdHolder,
                promotionOffers, digitalDeviceService, jsonUtils);

        userController.getUserInfo(UUID.randomUUID(), "835185ffd672ab9c");

        verify(userRegistrationServiceMock).getUserInfo(Mockito.any(), Mockito.eq("835185ffd672ab9c"));
    }

    @Test
    public void testGetUserInfo3() {
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setDigitalUserName("janedoe");
        userInfoResponse.setCoreCustomerProfileId(UUID.randomUUID());
        userInfoResponse.setDigitalCustomerStatusTypeId(1);
        userInfoResponse.setDigitalCustomerDeviceId("835185ffd672ab9c");

        Mono<UserInfoResponse> mono = Mono.just(userInfoResponse);
        UserRegistrationServiceImpl userRegistrationService1 = mock(UserRegistrationServiceImpl.class);
        when(userRegistrationService1.getUserInfo(Mockito.any(), Mockito.eq("835185ffd672ab9c")))
                .thenReturn(mono);

        UserController userController = new UserController(userRegistrationService1, userInfoService, pinService,
                digitalCustomerShortcutsService, digitalCookiesPreferenceService, auditDigitalCustomerIdHolder,
                promotionOffers, digitalDeviceService, jsonUtils);

        Mono<ResponseEntity<UserInfoResponse>> actualUserInfo = userController
                .getUserInfo(UUID.randomUUID(), "835185ffd672ab9c");

        verify(userRegistrationService1).getUserInfo(Mockito.any(), Mockito.eq("835185ffd672ab9c"));
        assertNotNull(actualUserInfo);
        actualUserInfo.subscribe(response -> {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("janedoe", response.getBody().getDigitalUserName());
            assertEquals("835185ffd672ab9c", response.getBody().getDigitalCustomerDeviceId());
        });
    }

}
