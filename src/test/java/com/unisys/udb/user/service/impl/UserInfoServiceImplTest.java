package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.config.AppConfig;
import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.BiometricStatusDTO;
import com.unisys.udb.user.dto.request.DeviceTokenRequest;
import com.unisys.udb.user.dto.request.DigitalPwdRequest;
import com.unisys.udb.user.dto.request.TermsConditionsAndCookiesRequest;
import com.unisys.udb.user.dto.request.UpdateExpiryDTO;
import com.unisys.udb.user.dto.request.UserDetailDto;
import com.unisys.udb.user.dto.request.UserStatusServiceRequest;
import com.unisys.udb.user.dto.response.CustomerDetail;
import com.unisys.udb.user.dto.response.CustomerDetailsResponse;
import com.unisys.udb.user.dto.response.DeRegisterDevicesResponse;
import com.unisys.udb.user.dto.response.DeviceDataForRegisterDevice;
import com.unisys.udb.user.dto.response.DeviceInfoResponse;
import com.unisys.udb.user.dto.response.DigitalCustomerPwdResponse;
import com.unisys.udb.user.dto.response.GlobalConfigResponse;
import com.unisys.udb.user.dto.response.NotificationOrchestratorResponse;
import com.unisys.udb.user.dto.response.TermsConditionsAndCookieResponse;
import com.unisys.udb.user.dto.response.UpdateExpiryResponse;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.dto.response.UserInfoResponse;
import com.unisys.udb.user.dto.response.UserLockResponse;
import com.unisys.udb.user.dto.response.UserStatusResponse;
import com.unisys.udb.user.dto.response.UserSuccessResponse;
import com.unisys.udb.user.entity.DigitalCustomerDevice;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import com.unisys.udb.user.entity.DigitalCustomerPwd;
import com.unisys.udb.user.entity.DigitalDeviceLink;
import com.unisys.udb.user.exception.ConfigurationServiceException;
import com.unisys.udb.user.exception.ConfigurationServiceUnavailableException;
import com.unisys.udb.user.exception.CustomerNotFoundException;
import com.unisys.udb.user.exception.DatabaseOperationsException;
import com.unisys.udb.user.exception.DigitalCustomerDeviceNotFoundException;
import com.unisys.udb.user.exception.DigitalPasswordStorageException;
import com.unisys.udb.user.exception.InvalidArgumentException;
import com.unisys.udb.user.exception.InvalidDataException;
import com.unisys.udb.user.exception.InvalidRequestException;
import com.unisys.udb.user.exception.InvalidUpdateField;
import com.unisys.udb.user.exception.InvalidUserException;
import com.unisys.udb.user.exception.MissingRequiredRequestParamException;
import com.unisys.udb.user.exception.PasswordExpiryException;
import com.unisys.udb.user.exception.UserLockedException;
import com.unisys.udb.user.exception.UserNameNotFoundException;
import com.unisys.udb.user.exception.WebClientIntegrationException;
import com.unisys.udb.user.exception.response.UdbExceptionResponse;
import com.unisys.udb.user.repository.DigitalCustomerDeviceAuditRepository;
import com.unisys.udb.user.repository.DigitalCustomerDeviceRepository;
import com.unisys.udb.user.repository.DigitalCustomerProfileRepository;
import com.unisys.udb.user.repository.DigitalCustomerPwdRepository;
import com.unisys.udb.user.repository.DigitalCustomerStatus;
import com.unisys.udb.user.repository.DigitalDeviceLinkRepository;
import com.unisys.udb.user.repository.LoginAttemptRepository;
import com.unisys.udb.user.repository.PinRepository;
import com.unisys.udb.user.repository.UserInfoRepository;
import com.unisys.udb.user.service.DigitalCustomerAlertService;
import com.unisys.udb.user.service.client.ConfigurationServiceClient;
import com.unisys.udb.user.service.client.NotificationOrchestratorServiceClient;
import com.unisys.udb.user.utils.dto.response.NotificationUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.unisys.udb.user.constants.UdbConstants.DATABASE_ERROR_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.OK_RESPONSE_CODE;
import static com.unisys.udb.user.constants.UdbConstants.ONE_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.PASSWORD_EXPIRY_DATE_UPDATED_SUCCESSFULLY;
import static com.unisys.udb.user.constants.UdbConstants.PIN_EXPIRY_DATE_UPDATED_SUCCESSFULLY;
import static com.unisys.udb.user.constants.UdbConstants.THREE_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.TWO_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.UNEXPECTED_ERROR_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.USER_LOCKED_SUCCESS_MESSAGE;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserInfoServiceImplTest {

    public static final int YEAR = 1970;
    private static final int INTERNAL_SERVER_ERROR_CODE = 500;
    private static final int SERVICE_UNAVAILABLE_ERROR_CODE = 503;
    @Mock
    private DigitalCustomerDeviceRepository digitalCustomerDeviceRepository;
    @Mock
    private DigitalDeviceLinkRepository digitalDeviceLinkRepository;
    @Mock
    private DigitalCustomerPwdRepository digitalCustomerPwdRepository;
    @Mock
    private LoginAttemptRepository loginAttemptRepository;
    @Mock
    private AppConfig appConfig;
    @Mock
    private DigitalCustomerProfileRepository digitalCustomerProfileRepository;
    @Mock
    private ConfigurationServiceClient configurationServiceClient;
    @Mock
    private DigitalCustomerAlertService digitalCustomerAlertService;
    @Mock
    private DigitalCustomerDeviceAuditRepository digitalCustomerDeviceAuditRepository;
    @InjectMocks
    private UserInfoServiceImpl userInfoService;
    @Mock
    private PinRepository pinRepository;
    private UUID digitalCustomerProfileId;
    private UserStatusServiceRequest userStatusServiceRequest;
    private DigitalCustomerProfile digitalCustomerProfile;
    @Mock
    private UserInfoRepository userInfoRepository;

    private Method handleWebClientExceptionMethod;
    private UUID testUUID;
    @Mock
    private NotificationUtil notificationUtil;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        handleWebClientExceptionMethod = UserInfoServiceImpl.class.getDeclaredMethod(
                "handleWebClientException", WebClientResponseException.class);
        handleWebClientExceptionMethod.setAccessible(true);
        userStatusServiceRequest = new UserStatusServiceRequest();
        digitalCustomerProfile = mock(DigitalCustomerProfile.class);
        MockitoAnnotations.openMocks(this);
        testUUID = UUID.randomUUID();
    }

    @Test
    void getUserInfoResponseDeviceNotFoundReturnsEmptyUserInfoResponse() {
        String deviceUdid = "3F2504E0-4F89-41D3-9A0C-0305E62D3318";
        Optional<DigitalCustomerDevice> digitalCustomerDevice = digitalCustomerDeviceRepository
                .findByDigitalDeviceUdid(deviceUdid);

        UserInfoResponse userInfoResponse = userInfoService.getUserInfoResponse(deviceUdid);
        when(digitalCustomerDeviceRepository.findByDigitalDeviceUdid(deviceUdid)).thenReturn(digitalCustomerDevice);
        assertNotNull(userInfoResponse);
        Assertions.assertNull(userInfoResponse.getDigitalCustomerProfileId());
    }


    @Test
    void getUserDeviceInfoDeviceFoundReturnsDeviceInfoResponse() {
        DigitalCustomerStatus digitalCustomerStatus;
        digitalCustomerProfileId = UUID.randomUUID();
        digitalCustomerStatus = mock(DigitalCustomerStatus.class);
        when(digitalCustomerStatus.getDeviceId()).thenReturn(1);
        when(digitalCustomerStatus.getDeviceType()).thenReturn("Mobile");
        when(digitalCustomerStatus.getDeviceToken()).thenReturn("token123");
        when(digitalCustomerDeviceRepository.findByProfileIdAndStatus(digitalCustomerProfileId))
                .thenReturn(List.of(digitalCustomerStatus));

        DeviceInfoResponse response = userInfoService.getUserDeviceInfo(digitalCustomerProfileId);

        assertNotNull(response);
        assertEquals(1, response.getDigitalCustomerDeviceId());
        assertEquals("Mobile", response.getDeviceType());
        assertEquals("token123", response.getDeviceToken());
    }

    @Test
    void getUserDeviceInfoDeviceNotFoundThrowsDigitalCustomerDeviceNotFoundException() {
        UUID profileId = UUID.randomUUID();
        when(digitalCustomerDeviceRepository.findByProfileIdAndStatus(profileId)).thenReturn(Collections.emptyList());

        Assertions.assertThrows(DigitalCustomerDeviceNotFoundException.class, () -> {
            userInfoService.getUserDeviceInfo(profileId);
        });
    }

    @Test
    void testUpdateTermsConditionsAndCookies() {
        DigitalCustomerDeviceRepository digitalCustomerDeviceRepository = mock(DigitalCustomerDeviceRepository.class);
        Optional<DigitalCustomerDevice> ofResult = Optional.of(mock(DigitalCustomerDevice.class));
        when(digitalCustomerDeviceRepository.findByDigitalDeviceUdid(Mockito.<String>any())).thenReturn(ofResult);
        UserInfoServiceImpl userInfoServiceImpl = userInfoServiceImpl();
        TermsConditionsAndCookiesRequest request = TermsConditionsAndCookiesRequest.builder()
                .deviceCreatedBy("Jan 1, 2020 8:00am GMT+0100")
                .deviceCreationDate("2020-03-01")
                .deviceModificationBy("Device Modification By")
                .deviceModificationDate("2020-03-01")
                .deviceName("Device Name")
                .deviceOsVersion("1.0.2")
                .deviceStatus(true)
                .deviceToken("ABC123")
                .deviceType("Device Type")
                .digitalCustomerDeviceId(1)
                .digitalDeviceUdid("Digital Device Udid")
                .functionalCookie(true)
                .performanceCookie(true)
                .strictlyAcceptanceCookie(true)
                .termsConditions(true)
                .build();
        assertThrows(InvalidUpdateField.class,
                () -> userInfoServiceImpl.updateTermsConditionsAndCookies(
                        "42", "2020-03-01", request));
    }

    @Test
    void testUpdateTermsConditionsAndCookies1() {
        DigitalCustomerDeviceRepository digitalCustomerDeviceRepository = mock(DigitalCustomerDeviceRepository.class);
        Optional<DigitalCustomerDevice> ofResult = Optional.of(mock(DigitalCustomerDevice.class));
        when(digitalCustomerDeviceRepository.findByDigitalDeviceUdid(Mockito.<String>any())).thenReturn(ofResult);
        UserInfoServiceImpl userInfoServiceImpl = userInfoServiceImpl();
        TermsConditionsAndCookiesRequest request = TermsConditionsAndCookiesRequest.builder()
                .deviceCreatedBy("Jan 1, 2020 8:00am GMT+0100")
                .deviceCreationDate("2020-03-01")
                .deviceModificationBy("Device Modification By")
                .deviceModificationDate("2020-03-01")
                .deviceName("Device Name")
                .deviceOsVersion("1.0.2")
                .deviceStatus(true)
                .deviceToken("ABC123")
                .deviceType("Device Type")
                .digitalCustomerDeviceId(1)
                .digitalDeviceUdid("Digital Device Udid")
                .functionalCookie(null)
                .performanceCookie(null)
                .strictlyAcceptanceCookie(null)
                .termsConditions(true)
                .build();
        assertThrows(MissingRequiredRequestParamException.class,
                () -> userInfoServiceImpl.updateTermsConditionsAndCookies("42", "cookies", request));

    }

    @Test
    void testUpdateTermsConditionsAndCookies3() {
        DigitalCustomerDeviceRepository digitalCustomerDeviceRepository = mock(DigitalCustomerDeviceRepository.class);
        Optional<DigitalCustomerDevice> ofResult = Optional.of(mock(DigitalCustomerDevice.class));
        when(digitalCustomerDeviceRepository.findByDigitalDeviceUdid(Mockito.<String>any())).thenReturn(ofResult);
        UserInfoServiceImpl userInfoServiceImpl = userInfoServiceImpl();
        TermsConditionsAndCookiesRequest request = TermsConditionsAndCookiesRequest.builder()
                .deviceCreatedBy("Jan 1, 2020 8:00am GMT+0100")
                .deviceCreationDate("2020-03-01")
                .deviceModificationBy("Device Modification By")
                .deviceModificationDate("2020-03-01")
                .deviceName("Device Name")
                .deviceOsVersion("1.0.2")
                .deviceStatus(true)
                .deviceToken("ABC123")
                .deviceType("Device Type")
                .digitalCustomerDeviceId(1)
                .digitalDeviceUdid("Digital Device Udid")
                .functionalCookie(null)
                .performanceCookie(null)
                .strictlyAcceptanceCookie(null)
                .termsConditions(null)
                .build();
        assertThrows(MissingRequiredRequestParamException.class,
                () -> userInfoServiceImpl.updateTermsConditionsAndCookies("42", "terms", request));
    }

    @Test
    void testHandleTermsConditionUpdate() {
        DigitalCustomerDevice digitalCustomerDevice = new DigitalCustomerDevice();
        digitalCustomerDevice.setDeviceCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerDevice.setDeviceCreationDate(LocalDate.now().atStartOfDay());
        digitalCustomerDevice.setDeviceModificationDate(LocalDate.now().atStartOfDay());
        digitalCustomerDevice.setDeviceModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCustomerDevice.setDeviceName("Device Name");
        digitalCustomerDevice.setDeviceOsVersion("1.0.2");
        digitalCustomerDevice.setDeviceFacePublicKey("Device Public Key");
        digitalCustomerDevice.setDeviceStatus(true);
        digitalCustomerDevice.setDeviceToken("ABC123");
        digitalCustomerDevice.setDeviceType("Device Type");
        digitalCustomerDevice.setDigitalCustomerDeviceId(1);
        digitalCustomerDevice.setDigitalDeviceLink(new DigitalDeviceLink());
        digitalCustomerDevice.setDigitalDeviceUdid("Digital Device Udid");
        digitalCustomerDevice.setFunctionalCookie(true);
        digitalCustomerDevice.setPerformanceCookie(true);
        digitalCustomerDevice.setStrictlyAcceptanceCookie(true);
        digitalCustomerDevice.setTermsAndConditions(true);

        DigitalCustomerProfile digitalCustomerProfile = new DigitalCustomerProfile();
        digitalCustomerProfile.setDigitalAccountStatusReason("Just cause");
        digitalCustomerProfile.setDigitalCustomerStatusTypeId(1);

        List<DigitalDeviceLink> digitalDeviceLinks = new ArrayList<>();
        digitalDeviceLinks.add(new DigitalDeviceLink());
        digitalCustomerProfile.setDigitalDeviceLink(digitalDeviceLinks);

        digitalCustomerProfile.setDigitalUserName("janedoe");
        digitalCustomerProfile.setMfaActivityCompleted(true);
        digitalCustomerProfile.setPinSetCompleted(true);
        digitalCustomerProfile.setProfileCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerProfile.setProfileModificationDate(LocalDate.now().atStartOfDay());
        digitalCustomerProfile.setProfileModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCustomerProfile.setRegistrationDate(LocalDate.now().atStartOfDay());

        DigitalDeviceLink digitalDeviceLink = new DigitalDeviceLink();
        digitalDeviceLink.setDeviceLinkCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalDeviceLink.setDeviceLinkCreationDate(LocalDate.now().atStartOfDay());
        digitalDeviceLink.setDeviceLinkModificationDate(LocalDate.now().atStartOfDay());
        digitalDeviceLink.setDeviceLinkModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalDeviceLink.setDigitalCustomerDevice(digitalCustomerDevice);
        digitalDeviceLink.setDigitalCustomerProfile(digitalCustomerProfile);
        digitalDeviceLink.setDigitalDeviceLinkId(1);

        DigitalCustomerDeviceRepository digitalCustomerDeviceRepository = mock(DigitalCustomerDeviceRepository.class);
        when(digitalCustomerDeviceRepository.save(Mockito.<DigitalCustomerDevice>any())).thenReturn(
                digitalCustomerDevice);
        UserInfoServiceImpl userInfoServiceImpl = userInfoServiceImpl();

        TermsConditionsAndCookiesRequest request = new TermsConditionsAndCookiesRequest();
        DigitalCustomerDevice digitalCustomerDevice5 = mock(DigitalCustomerDevice.class);
        doNothing().when(digitalCustomerDevice5).setTermsAndConditions(Mockito.<Boolean>any());
        Optional<DigitalCustomerDevice> deviceById = Optional.of(digitalCustomerDevice5);
        TermsConditionsAndCookieResponse actualHandleTermsConditionUpdateResult = userInfoServiceImpl
                .handleTermsConditionUpdate(request, deviceById);

        assertEquals("Success", actualHandleTermsConditionUpdateResult.getMessage());
        assertEquals(HttpStatus.OK, actualHandleTermsConditionUpdateResult.getStatus());
    }


    @Test
    void testHandleTermsConditionUpdate2() {
        UserInfoServiceImpl userInfoServiceImpl = userInfoServiceImpl();
        TermsConditionsAndCookiesRequest request = new TermsConditionsAndCookiesRequest();
        DigitalCustomerDevice digitalCustomerDevice = mock(DigitalCustomerDevice.class);
        doThrow(new DigitalCustomerDeviceNotFoundException((UUID) null)).when(digitalCustomerDevice)
                .setTermsAndConditions(Mockito.<Boolean>any());
        Optional<DigitalCustomerDevice> deviceById = Optional.of(digitalCustomerDevice);
        assertThrows(DigitalCustomerDeviceNotFoundException.class,
                () -> userInfoServiceImpl.handleTermsConditionUpdate(request, deviceById));
        verify(digitalCustomerDevice).setTermsAndConditions(Mockito.<Boolean>any());
    }

    @Test
    void testUpdateTermsAndConditions() {
        DigitalCustomerDevice digitalCustomerDevice = new DigitalCustomerDevice();
        digitalCustomerDevice.setDeviceCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerDevice.setDeviceCreationDate(LocalDate.now().atStartOfDay());
        digitalCustomerDevice.setDeviceModificationDate(LocalDate.now().atStartOfDay());
        digitalCustomerDevice.setDeviceModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCustomerDevice.setDeviceName("Device Name");
        digitalCustomerDevice.setDeviceOsVersion("1.0.2");
        digitalCustomerDevice.setDeviceFacePublicKey("Device Public Key");
        digitalCustomerDevice.setDeviceStatus(true);
        digitalCustomerDevice.setDeviceToken("ABC123");
        digitalCustomerDevice.setDeviceType("Device Type");
        digitalCustomerDevice.setDigitalCustomerDeviceId(1);
        digitalCustomerDevice.setDigitalDeviceLink(new DigitalDeviceLink());
        digitalCustomerDevice.setDigitalDeviceUdid("Digital Device Udid");
        digitalCustomerDevice.setFunctionalCookie(true);
        digitalCustomerDevice.setPerformanceCookie(true);
        digitalCustomerDevice.setStrictlyAcceptanceCookie(true);
        digitalCustomerDevice.setTermsAndConditions(true);

        DigitalCustomerProfile digitalCustomerProfile = new DigitalCustomerProfile();
        digitalCustomerProfile.setDigitalAccountStatusReason("Just cause");
        digitalCustomerProfile.setDigitalCustomerStatusTypeId(1);

        List<DigitalDeviceLink> digitalDeviceLinks = new ArrayList<>();
        digitalDeviceLinks.add(new DigitalDeviceLink());
        digitalCustomerProfile.setDigitalDeviceLink(digitalDeviceLinks);

        digitalCustomerProfile.setDigitalUserName("janedoe");
        digitalCustomerProfile.setMfaActivityCompleted(true);
        digitalCustomerProfile.setPinSetCompleted(true);
        digitalCustomerProfile.setProfileCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerProfile.setProfileModificationDate(LocalDate.now().atStartOfDay());
        digitalCustomerProfile.setProfileModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCustomerProfile.setRegistrationDate(LocalDate.now().atStartOfDay());

        DigitalDeviceLink digitalDeviceLink = new DigitalDeviceLink();
        digitalDeviceLink.setDeviceLinkCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalDeviceLink.setDeviceLinkCreationDate(LocalDate.now().atStartOfDay());
        digitalDeviceLink.setDeviceLinkModificationDate(LocalDate.now().atStartOfDay());
        digitalDeviceLink.setDeviceLinkModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalDeviceLink.setDigitalCustomerDevice(digitalCustomerDevice);
        digitalDeviceLink.setDigitalCustomerProfile(digitalCustomerProfile);
        digitalDeviceLink.setDigitalDeviceLinkId(1);


        when(digitalCustomerDeviceRepository.save(Mockito.<DigitalCustomerDevice>any()))
                .thenReturn(digitalCustomerDevice);
        UserInfoServiceImpl userInfoServiceImpl = userInfoServiceImpl();

        TermsConditionsAndCookiesRequest request = new TermsConditionsAndCookiesRequest();
        DigitalCustomerDevice digitalCustomerDevice5 = mock(DigitalCustomerDevice.class);
        doNothing().when(digitalCustomerDevice5).setTermsAndConditions(Mockito.<Boolean>any());
        Optional<DigitalCustomerDevice> deviceById = Optional.of(digitalCustomerDevice5);

        userInfoServiceImpl.updateTermsAndConditions(request, deviceById);

        verify(digitalCustomerDevice5).setTermsAndConditions(Mockito.<Boolean>any());
    }


    @Test
    void testUpdateTermsAndConditions2() {
        UserInfoServiceImpl userInfoServiceImpl = userInfoServiceImpl();
        TermsConditionsAndCookiesRequest request = new TermsConditionsAndCookiesRequest();
        DigitalCustomerDevice digitalCustomerDevice = mock(DigitalCustomerDevice.class);
        doThrow(new DigitalCustomerDeviceNotFoundException((UUID) null)).when(digitalCustomerDevice)
                .setTermsAndConditions(Mockito.<Boolean>any());
        Optional<DigitalCustomerDevice> deviceById = Optional.of(digitalCustomerDevice);
        assertThrows(DigitalCustomerDeviceNotFoundException.class,
                () -> userInfoServiceImpl.updateTermsAndConditions(request, deviceById));
        verify(digitalCustomerDevice).setTermsAndConditions(Mockito.<Boolean>any());
    }

    @Test
    void testHandleCookies() {
        DigitalCustomerDevice digitalCustomerDevice = new DigitalCustomerDevice();
        digitalCustomerDevice.setDeviceCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerDevice.setDeviceCreationDate(LocalDate.now().atStartOfDay());
        digitalCustomerDevice.setDeviceModificationDate(LocalDate.now().atStartOfDay());
        digitalCustomerDevice.setDeviceModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCustomerDevice.setDeviceName("Device Name");
        digitalCustomerDevice.setDeviceOsVersion("1.0.2");
        digitalCustomerDevice.setDeviceFacePublicKey("Device Public Key");
        digitalCustomerDevice.setDeviceStatus(true);
        digitalCustomerDevice.setDeviceToken("ABC123");
        digitalCustomerDevice.setDeviceType("Device Type");
        digitalCustomerDevice.setDigitalCustomerDeviceId(1);
        digitalCustomerDevice.setDigitalDeviceLink(new DigitalDeviceLink());
        digitalCustomerDevice.setDigitalDeviceUdid("Digital Device Udid");
        digitalCustomerDevice.setFunctionalCookie(true);
        digitalCustomerDevice.setPerformanceCookie(true);
        digitalCustomerDevice.setStrictlyAcceptanceCookie(true);
        digitalCustomerDevice.setTermsAndConditions(true);

        DigitalCustomerProfile digitalCustomerProfile = new DigitalCustomerProfile();
        digitalCustomerProfile.setDigitalAccountStatusReason("Just cause");
        digitalCustomerProfile.setDigitalCustomerStatusTypeId(1);

        List<DigitalDeviceLink> digitalDeviceLinks = new ArrayList<>();
        digitalDeviceLinks.add(new DigitalDeviceLink());
        digitalCustomerProfile.setDigitalDeviceLink(digitalDeviceLinks);

        digitalCustomerProfile.setDigitalUserName("janedoe");
        digitalCustomerProfile.setMfaActivityCompleted(true);
        digitalCustomerProfile.setPinSetCompleted(true);
        digitalCustomerProfile.setProfileCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerProfile.setProfileModificationDate(LocalDate.now().atStartOfDay());
        digitalCustomerProfile.setProfileModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCustomerProfile.setRegistrationDate(LocalDate.now().atStartOfDay());

        DigitalDeviceLink digitalDeviceLink = new DigitalDeviceLink();
        digitalDeviceLink.setDeviceLinkCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalDeviceLink.setDeviceLinkCreationDate(LocalDate.now().atStartOfDay());
        digitalDeviceLink.setDeviceLinkModificationDate(LocalDate.now().atStartOfDay());
        digitalDeviceLink.setDeviceLinkModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalDeviceLink.setDigitalCustomerDevice(digitalCustomerDevice);
        digitalDeviceLink.setDigitalCustomerProfile(digitalCustomerProfile);
        digitalDeviceLink.setDigitalDeviceLinkId(1);

        DigitalCustomerDeviceRepository digitalCustomerDeviceRepository = mock(DigitalCustomerDeviceRepository.class);
        when(digitalCustomerDeviceRepository.save(Mockito.<DigitalCustomerDevice>any()))
                .thenReturn(digitalCustomerDevice);
        UserInfoServiceImpl userInfoServiceImpl = userInfoServiceImpl();

        TermsConditionsAndCookiesRequest request = new TermsConditionsAndCookiesRequest();
        DigitalCustomerDevice digitalCustomerDevice5 = mock(DigitalCustomerDevice.class);
        doNothing().when(digitalCustomerDevice5).setFunctionalCookie(Mockito.<Boolean>any());
        doNothing().when(digitalCustomerDevice5).setPerformanceCookie(Mockito.<Boolean>any());
        doNothing().when(digitalCustomerDevice5).setStrictlyAcceptanceCookie(Mockito.<Boolean>any());
        Optional<DigitalCustomerDevice> deviceById = Optional.of(digitalCustomerDevice5);
        TermsConditionsAndCookieResponse actualHandleCookiesResult =
                userInfoServiceImpl.handleCookies(request, deviceById);

        assertEquals("Success", actualHandleCookiesResult.getMessage());
        assertEquals(HttpStatus.OK, actualHandleCookiesResult.getStatus());
    }


    @Test
    void testHandleCookies2() {
        UserInfoServiceImpl userInfoServiceImpl = userInfoServiceImpl();
        TermsConditionsAndCookiesRequest request = new TermsConditionsAndCookiesRequest();
        DigitalCustomerDevice digitalCustomerDevice = mock(DigitalCustomerDevice.class);
        doThrow(new MissingRequiredRequestParamException("Success")).when(digitalCustomerDevice)
                .setStrictlyAcceptanceCookie(Mockito.<Boolean>any());
        Optional<DigitalCustomerDevice> deviceById = Optional.of(digitalCustomerDevice);
        assertThrows(MissingRequiredRequestParamException.class,
                () -> userInfoServiceImpl.handleCookies(request, deviceById));
        verify(digitalCustomerDevice).setStrictlyAcceptanceCookie(Mockito.<Boolean>any());
    }

    @Test
    void testUpdateCookies() {
        // Create a mock request
        TermsConditionsAndCookiesRequest request = new TermsConditionsAndCookiesRequest();
        request.setStrictlyAcceptanceCookie(true);
        request.setFunctionalCookie(true);
        request.setPerformanceCookie(true);

        // Create a mock DigitalCustomerDevice
        DigitalCustomerDevice digitalCustomerDevice = new DigitalCustomerDevice();
        digitalCustomerDevice.setDeviceCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerDevice.setDeviceCreationDate(LocalDate.of(YEAR, 1, 1).atStartOfDay());
        digitalCustomerDevice.setDeviceModificationDate(LocalDate.of(YEAR, 1, 1).atStartOfDay());
        digitalCustomerDevice.setDeviceModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCustomerDevice.setDeviceName("Device Name");
        digitalCustomerDevice.setDeviceOsVersion("1.0.2");
        digitalCustomerDevice.setDeviceFacePublicKey("Device Public Key");
        digitalCustomerDevice.setDeviceStatus(true);
        digitalCustomerDevice.setDeviceToken("ABC123");
        digitalCustomerDevice.setDeviceType("Device Type");
        digitalCustomerDevice.setDigitalCustomerDeviceId(1);
        digitalCustomerDevice.setDigitalDeviceLink(new DigitalDeviceLink());
        digitalCustomerDevice.setDigitalDeviceUdid("Digital Device Udid");
        digitalCustomerDevice.setFunctionalCookie(false);
        digitalCustomerDevice.setPerformanceCookie(false);
        digitalCustomerDevice.setStrictlyAcceptanceCookie(false);
        digitalCustomerDevice.setTermsAndConditions(true);

        // Mock the repository
        when(digitalCustomerDeviceRepository.save(Mockito.<DigitalCustomerDevice>any()))
                .thenReturn(digitalCustomerDevice);

        DigitalDeviceLink digitalDeviceLink = new DigitalDeviceLink();
        digitalDeviceLink.setDeviceLinkCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalDeviceLink.setDeviceLinkCreationDate(LocalDate.of(YEAR, 1, 1).atStartOfDay());
        digitalDeviceLink.setDeviceLinkModificationDate(LocalDate.of(YEAR, 1, 1).atStartOfDay());
        digitalDeviceLink.setDeviceLinkModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalDeviceLink.setDigitalCustomerDevice(digitalCustomerDevice);
        digitalDeviceLink.setDigitalCustomerProfile(digitalCustomerProfile);
        digitalDeviceLink.setDigitalDeviceLinkId(1);

        DigitalCustomerDeviceRepository digitalCustomerDeviceRepository = mock(DigitalCustomerDeviceRepository.class);
        when(digitalCustomerDeviceRepository.save(Mockito.<DigitalCustomerDevice>any())).thenReturn(
                digitalCustomerDevice);
        UserInfoServiceImpl userInfoServiceImpl = userInfoServiceImpl();
        DigitalCustomerDevice digitalCustomerDevice5 = mock(DigitalCustomerDevice.class);
        doNothing().when(digitalCustomerDevice5).setFunctionalCookie(Mockito.<Boolean>any());
        doNothing().when(digitalCustomerDevice5).setPerformanceCookie(Mockito.<Boolean>any());
        doNothing().when(digitalCustomerDevice5).setStrictlyAcceptanceCookie(Mockito.<Boolean>any());
        Optional<DigitalCustomerDevice> deviceById = Optional.of(digitalCustomerDevice5);
        userInfoServiceImpl.updateCookies(request, deviceById);
        verify(digitalCustomerDevice5).setFunctionalCookie(Mockito.<Boolean>any());
        verify(digitalCustomerDevice5).setPerformanceCookie(Mockito.<Boolean>any());
        verify(digitalCustomerDevice5).setStrictlyAcceptanceCookie(Mockito.<Boolean>any());

    }


    @Test
    void testUpdateCookies2() {
        UserInfoServiceImpl userInfoServiceImpl = userInfoServiceImpl();
        TermsConditionsAndCookiesRequest request = new TermsConditionsAndCookiesRequest();
        DigitalCustomerDevice digitalCustomerDevice = mock(DigitalCustomerDevice.class);
        doThrow(new MissingRequiredRequestParamException("foo")).when(digitalCustomerDevice)
                .setStrictlyAcceptanceCookie(Mockito.<Boolean>any());
        Optional<DigitalCustomerDevice> deviceById = Optional.of(digitalCustomerDevice);
        assertThrows(MissingRequiredRequestParamException.class,
                () -> userInfoServiceImpl.updateCookies(request, deviceById));
        verify(digitalCustomerDevice).setStrictlyAcceptanceCookie(Mockito.<Boolean>any());
    }

    @Test
    void testGetUserStatusSuccess() {
        String digitalUserName = "testUser";
        String accountStatus = "ACTIVE";

        DigitalCustomerProfile digitalCustomerProfile = new DigitalCustomerProfile();
        digitalCustomerProfile.setPinExpiryDate(LocalDateTime.now());
        digitalCustomerProfile.setPwdExpiryDate(LocalDateTime.now());

        when(userInfoRepository.getCustomerStatusTypeByDigitalUserName(digitalUserName))
                .thenReturn(Optional.of(accountStatus));

        when(userInfoRepository.findByDigitalUserName(anyString())).thenReturn(digitalCustomerProfile);

        Mono<UserStatusResponse> responseMono = userInfoService.
                getUserStatus(digitalUserName);

        UserStatusResponse response = responseMono.block();
        assertNotNull(response);
        assertEquals(accountStatus, response.getAccountStatus());

        verify(userInfoRepository, times(1))
                .getCustomerStatusTypeByDigitalUserName(digitalUserName);
    }

    @Test
    void testHandleInternalServerWebclientException() {
        // Arrang
        String digitalUserName = "testUser";
        String accountStatus = "ACTIVE";

        // Mock ConfigurationServiceClient to throw WebClientResponseException
        when(configurationServiceClient.getGlobalConfig("UDB_PWD_EXPRY_PERIOD"))
                .thenThrow(WebClientResponseException.create(
                        INTERNAL_SERVER_ERROR_CODE, "Internal Server Error",
                        null, null, null));

        when(userInfoRepository.getCustomerStatusTypeByDigitalUserName(digitalUserName))
                .thenReturn(Optional.of(accountStatus));

        // Act & Assert
        assertThrows(Exception.class, () -> {
            userInfoService.getUserStatus("testUser").block();
        });
    }

    @Test
    void testHandleServiceUnavailableWebclientException() {
        // Arrange
        String digitalUserName = "testUser";
        String accountStatus = "ACTIVE";

        // Mock ConfigurationServiceClient to throw WebClientResponseException
        when(configurationServiceClient.getGlobalConfig("UDB_PWD_EXPRY_PERIOD"))
                .thenThrow(WebClientResponseException.create(
                        SERVICE_UNAVAILABLE_ERROR_CODE, "Service unavailable Error",
                        null, null, null));

        when(userInfoRepository.getCustomerStatusTypeByDigitalUserName(digitalUserName))
                .thenReturn(Optional.of(accountStatus));

        // Act & Assert
        assertThrows(Exception.class, () -> {
            userInfoService.getUserStatus("testUser").block();
        });
    }

    @Test
    void testHandleWebClientException() {
        // Arrange
        String digitalUserName = "testUser";
        String accountStatus = "ACTIVE";

        // Mock ConfigurationServiceClient to throw WebClientResponseException
        when(configurationServiceClient.getGlobalConfig("UDB_PWD_EXPRY_PERIOD"))
                .thenThrow(WebClientResponseException.create(
                        INTERNAL_SERVER_ERROR_CODE, "Internal Server Error",
                        null, null, null));

        when(userInfoRepository.getCustomerStatusTypeByDigitalUserName(digitalUserName))
                .thenReturn(Optional.of(accountStatus));

        // Act & Assert
        assertThrows(Exception.class, () -> {
            userInfoService.getUserStatus("testUser").block();
        });

    }

    @Test
    void testGetUserStatusNotFound() {
        String digitalUserName = "AP@gmail.com";
        when(userInfoRepository.getCustomerStatusTypeByDigitalUserName(digitalUserName))
                .thenReturn(Optional.empty());

        assertThrows(UserNameNotFoundException.class,
                () -> userInfoService.getUserStatus(digitalUserName));

        verify(userInfoRepository).getCustomerStatusTypeByDigitalUserName(digitalUserName);
    }

    @Test
    void testUpdateDeviceTokenSuccess() {
        DigitalCustomerDevice digitalCustomerDevice = mock(DigitalCustomerDevice.class);
        doNothing().when(digitalCustomerDevice).setDeviceToken(Mockito.<String>any());

        DigitalCustomerDevice digitalCustomerDevice2 = new DigitalCustomerDevice();
        digitalCustomerDevice2.setDeviceCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerDevice2.setDeviceCreationDate(LocalDate.of(YEAR, 1, 1).atStartOfDay());
        digitalCustomerDevice2.setDeviceModificationDate(LocalDate.of(YEAR, 1, 1).atStartOfDay());
        digitalCustomerDevice2.setDeviceModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCustomerDevice2.setDeviceName("Device Name");
        digitalCustomerDevice2.setDeviceOsVersion("1.0.2");
        digitalCustomerDevice2.setDeviceFacePublicKey("Device Public Key");
        digitalCustomerDevice2.setDeviceStatus(true);
        digitalCustomerDevice2.setDeviceToken("ABC123");
        digitalCustomerDevice2.setDeviceType("Device Type");
        digitalCustomerDevice2.setDigitalCustomerDeviceId(1);
        digitalCustomerDevice2.setDigitalDeviceLink(new DigitalDeviceLink());
        digitalCustomerDevice2.setDigitalDeviceUdid("Digital Device Udid");
        digitalCustomerDevice2.setFunctionalCookie(true);
        digitalCustomerDevice2.setPerformanceCookie(true);
        digitalCustomerDevice2.setStrictlyAcceptanceCookie(true);
        digitalCustomerDevice2.setTermsAndConditions(true);

        DeviceTokenRequest request = DeviceTokenRequest.builder()
                .digitalDeviceUdId("sample-udid")
                .deviceToken("sample-token")
                .build();
        when(digitalCustomerDeviceRepository.findByDigitalDeviceUdid(anyString()))
                .thenReturn(Optional.of(digitalCustomerDevice));

        Mono<UserAPIBaseResponse> responseMono = userInfoService.updateDeviceToken(request);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getHttpStatus());
                    assertEquals("Device Token Updated Successfully", response.getMessage());
                })
                .verifyComplete();

        verify(digitalCustomerDeviceRepository, times(1)).saveAndFlush(digitalCustomerDevice);
    }

    @Test
    void testUpdateDeviceTokenMissingToken() {
        DeviceTokenRequest request = DeviceTokenRequest.builder()
                .digitalDeviceUdId(" ")
                .deviceToken(" ")
                .build();

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class,
                () -> userInfoService.updateDeviceToken(request));

        assertEquals("Invalid request payload", exception.getMessage());

    }

    @Test
    void updateLoginAttemptsInvalidAttemptCountOneThrowsInvalidUserException() {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setUserName("testUserName");
        userDetailDto.setUserMessage("testMessage");
        userDetailDto.setDigitalCustomerDeviceId("3F2504E0-4F89-41D3-9A0C-0305E62D3318");
        when(loginAttemptRepository.updateLoginAttemptDetails(any(), any(), any(), any()))
                .thenReturn(ONE_CONSTANT);

        Assertions.assertThrows(UserLockedException.class, () -> {
            userInfoService.updateFailureAttemptDetailsByUsername(userDetailDto);
        });
    }

    @Test
    void updateLoginAttemptsInvalidAttemptCountTwoThrowsInvalidUserException() {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setUserName("testUserName");
        userDetailDto.setUserMessage("testMessage");
        userDetailDto.setDigitalCustomerDeviceId("3F2504E0-4F89-41D3-9A0C-0305E62D3318");
        when(loginAttemptRepository.updateLoginAttemptDetails(any(), any(), any(), any()))
                .thenReturn(TWO_CONSTANT);

        Assertions.assertThrows(UserLockedException.class, () -> {
            userInfoService.updateFailureAttemptDetailsByUsername(userDetailDto);
        });
    }

    @Test
    void updateFailureAttemptDetailsByUsernameMaxAttemptCountReachedThrowsUserLockedException() {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setUserName("testUserName");
        userDetailDto.setUserMessage("testMessage");
        userDetailDto.setDigitalCustomerDeviceId("3F2504E0-4F89-41D3-9A0C-0305E62D3318");
        when(appConfig.getLoginCount()).thenReturn(THREE_CONSTANT);
        when(loginAttemptRepository.updateLoginAttemptDetails(any(), any(), any(), any()))
                .thenReturn(THREE_CONSTANT);
        NotificationOrchestratorServiceClient notificationOrchestratorServiceClient = mock(
                NotificationOrchestratorServiceClient.class);
        DigitalCustomerDeviceAuditRepository digitalCustomerDeviceAuditRepository =
                mock(DigitalCustomerDeviceAuditRepository.class);
        UserInfoServiceImpl userInfoServiceImpl = userInfoServiceImpl();
        NotificationOrchestratorResponse res = new NotificationOrchestratorResponse();
        res.setHttpStatus(HttpStatus.OK);
        res.setStatus("Success");
        res.setMessage("Published to kafka");
        when(notificationOrchestratorServiceClient.publishNotification(Mockito.any()))
                .thenReturn(res);
        Assertions.assertThrows(UserLockedException.class, () -> {
            userInfoServiceImpl.updateFailureAttemptDetailsByUsername(userDetailDto);
        });
    }

    @Test
    void updateFailureAttemptDetailsByUsernameAttemptCountEqualsMaxMinusOneThrowsInvalidUserException() {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setUserName("testUserName");
        userDetailDto.setUserMessage("testMessage");
        userDetailDto.setDigitalCustomerDeviceId("3F2504E0-4F89-41D3-9A0C-0305E62D3318");
        when(appConfig.getLoginCount()).thenReturn(THREE_CONSTANT);
        when(loginAttemptRepository.updateLoginAttemptDetails(any(), any(), any(), any()))
                .thenReturn(THREE_CONSTANT);

        Assertions.assertThrows(UserLockedException.class, () -> {
            userInfoService.updateFailureAttemptDetailsByUsername(userDetailDto);
        });
    }

    @Test
    void updateFailureAttemptDetailsByUsernameAttemptCountLessThanMaxMinusOneThrowsInvalidUserException() {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setUserName("testUserName");
        userDetailDto.setUserMessage("testMessage");
        userDetailDto.setDigitalCustomerDeviceId("3F2504E0-4F89-41D3-9A0C-0305E62D3318");
        when(appConfig.getLoginCount()).thenReturn(THREE_CONSTANT);
        when(loginAttemptRepository.updateLoginAttemptDetails(any(), any(), any(), any()))
                .thenReturn(ONE_CONSTANT);
        doThrow(new RuntimeException("Test Exception")).when(digitalCustomerAlertService)
                .saveDigitalCustomerAlert(any());

        Assertions.assertThrows(InvalidUserException.class, () -> {
            userInfoService.updateFailureAttemptDetailsByUsername(userDetailDto);
        });
    }

    @Test
    void getUserInfoResponseDeviceAndLinkFoundReturnsUserInfoResponse() {
        String deviceUdid = "3F2504E0-4F89-41D3-9A0C-0305E62D3318";
        DigitalCustomerDevice digitalCustomerDevice = new DigitalCustomerDevice();
        when(digitalCustomerDeviceRepository.findByDigitalDeviceUdid(deviceUdid))
                .thenReturn(Optional.of(digitalCustomerDevice));

        DigitalDeviceLink digitalDeviceLink = new DigitalDeviceLink();
        DigitalCustomerProfile digitalCustomerProfile = new DigitalCustomerProfile();
        digitalDeviceLink.setDigitalCustomerProfile(digitalCustomerProfile);
        digitalDeviceLink.setDigitalCustomerDevice(digitalCustomerDevice);

        // Create a list and add the digitalDeviceLink to it
        List<DigitalDeviceLink> digitalDeviceLinks = new ArrayList<>();
        digitalDeviceLinks.add(digitalDeviceLink);
        digitalCustomerProfile.setDigitalDeviceLink(digitalDeviceLinks);

        when(digitalDeviceLinkRepository.findByDigitalCustomerDevice(digitalCustomerDevice))
                .thenReturn(digitalDeviceLink);

        UserInfoResponse userInfoResponse = userInfoService.getUserInfoResponse(deviceUdid);

        assertNotNull(userInfoResponse);
        // Add more assertions for other fields as needed
    }


    @Test
    void updateLoginAttemptsInvalidAttemptCountThreeThrowsUserLockedException() {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setUserName("testUserName");
        userDetailDto.setUserMessage("testMessage");
        userDetailDto.setDigitalCustomerDeviceId("3F2504E0-4F89-41D3-9A0C-0305E62D3318");
        when(loginAttemptRepository.updateLoginAttemptDetails(any(), any(), any(), any()))
                .thenReturn(THREE_CONSTANT); // Make sure the mocked method returns THREE_CONSTANT

        Assertions.assertThrows(UserLockedException.class, () -> {
            userInfoService.updateFailureAttemptDetailsByUsername(userDetailDto);
        });
    }

    @Test
    void testUpdateFailureAttemptDetailsByUsernameInvalidUserMaxAttemptMinusOne() {
        UserDetailDto userDetailDto = new UserDetailDto();
        when(appConfig.getLoginCount()).thenReturn(THREE_CONSTANT);
        when(loginAttemptRepository.updateLoginAttemptDetails(any(), any(), any(), any())).thenReturn(TWO_CONSTANT);

        InvalidUserException exception = assertThrows(InvalidUserException.class, () -> {
            userInfoService.updateFailureAttemptDetailsByUsername(userDetailDto);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());
        assertEquals("failure", exception.getResponseType());
        assertEquals("Invalid Credentials", exception.getStatusMessage());
    }

    @Test
    void testUpdateFailureAttemptDetailsByUsernameInvalidUserMaxAttemptMinusTwo() {
        UserDetailDto userDetailDto = new UserDetailDto();
        when(appConfig.getLoginCount()).thenReturn(THREE_CONSTANT);
        when(loginAttemptRepository.updateLoginAttemptDetails(any(), any(), any(), any())).thenReturn(ONE_CONSTANT);

        InvalidUserException exception = assertThrows(InvalidUserException.class, () -> {
            userInfoService.updateFailureAttemptDetailsByUsername(userDetailDto);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());
        assertEquals("failure", exception.getResponseType());
        assertEquals("Invalid Credentials", exception.getStatusMessage());
    }


    @Test
    void testUpdateFailureAttemptDetailsByUsernameKafkaError() {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setUserName("testUserName");
        when(appConfig.getLoginCount()).thenReturn(THREE_CONSTANT);
        when(loginAttemptRepository.updateLoginAttemptDetails(any(), any(), any(), any()))
                .thenReturn(THREE_CONSTANT);
        NotificationOrchestratorServiceClient notificationOrchestratorServiceClient = mock(
                NotificationOrchestratorServiceClient.class);
        DigitalCustomerDeviceAuditRepository digitalCustomerDeviceAuditRepository =
                mock(DigitalCustomerDeviceAuditRepository.class);
        UserInfoServiceImpl userInfoServiceImpl = userInfoServiceImpl();
        String responseBody = "{"
                + "    \"timestamp\": \"2024-04-19T05:10:43.044+00:00\","
                + "    \"status\": 500,"
                + "    \"message\": \"Publish to kafka failed\","
                + "}";
        WebClientResponseException exception = WebClientResponseException.create(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                null, responseBody.getBytes(), null);
        when(notificationOrchestratorServiceClient.publishNotification(Mockito.any()))
                .thenThrow(exception);
        assertThrows(UserLockedException.class, () -> {
            userInfoServiceImpl.updateFailureAttemptDetailsByUsername(userDetailDto);
        });
    }


    @Test
    void getCustomerDetailsBySearchTermValidSearchTermSuccess() {
        // Mocking repository response
        List<Object[]> mockDetails = new ArrayList<>();
        Object[] mockDetail = new Object[]{"sofiya Elizabeth Brow", "sundeep.kumar@unisys.com",
                "123e4567-e89b-12d3-a456-426614174000", "S3 Ultra", Timestamp.valueOf("2024-03-13 14:26:25.500"),
                "Active", "919110453514"};
        mockDetails.add(mockDetail);
        when(userInfoRepository.findCustomerDetailsBySearchTerm("sofiya")).thenReturn(mockDetails);

        // Calling the service method
        CustomerDetailsResponse response = userInfoService.getCustomerDetailsBySearchTerm("sofiya");

        // Assertions
        assertNotNull(response);
        CustomerDetail customerDetail = response.getCustomerDetails().get(0);
        assertEquals("sofiya Elizabeth Brow", customerDetail.getName());
        assertEquals("919110453514", customerDetail.getPhoneNumber());
        assertEquals("123e4567-e89b-12d3-a456-426614174000", customerDetail
                .getDigitalCustomerProfileId().toString());


        // Verify that the repository method was called once
        verify(userInfoRepository, times(1)).findCustomerDetailsBySearchTerm("sofiya");
    }

    @Test
    void getCustomerDetailsBySearchTermInvalidSearchTerm() {
        // Mocking repository response for an empty result
        when(userInfoRepository.findCustomerDetailsBySearchTerm("Unknown")).thenReturn(new ArrayList<>());

        // Calling the service method with an invalid search term
        assertThrows(CustomerNotFoundException.class, () -> {
            userInfoService.getCustomerDetailsBySearchTerm("Unknown");
        });

        // Verify that the repository method was called once
        verify(userInfoRepository, times(1)).findCustomerDetailsBySearchTerm("Unknown");
    }

    @Test
    void testValidateUserNameSuccess() {
        when(digitalCustomerProfileRepository.existsByDigitalUserName(anyString())).thenReturn(true);

        UserSuccessResponse result = userInfoService.validateUserName("Jon doe");

        assertEquals(new UserSuccessResponse("User name is valid"), result);

    }

    @Test
    void testValidateUserNameFailure() {
        when(digitalCustomerProfileRepository.existsByDigitalUserName(anyString())).thenReturn(false);
        assertThrows(InvalidRequestException.class, () -> userInfoService.validateUserName("Jon doe"));

    }

    @Test
    void testStoreOldPasswordSuccess() {
        // Arrange
        UUID digitalProfileIdString = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");
        String password = "myOldPassword";
        String userName = "testUser";
        DigitalPwdRequest request = DigitalPwdRequest.builder()
                .digitalProfileId(digitalProfileIdString)
                .password(password)
                .build();

        DigitalCustomerPwd digitalCustomerPwd = new DigitalCustomerPwd();
        digitalCustomerPwd.setDigitalCustomerProfileId(digitalProfileIdString);
        digitalCustomerPwd.setEncryptedOldPassword(new BCryptPasswordEncoder().encode(password));
        LocalDateTime currentDate = LocalDateTime.now();
        digitalCustomerPwd.setPasswordChangeDate(java.sql.Timestamp.valueOf(currentDate));
        digitalCustomerPwd.setPasswordExpiryDate(java.sql.Timestamp.valueOf(currentDate));
        digitalCustomerPwd.setPasswordCreationDate(java.sql.Timestamp.valueOf(currentDate));
        digitalCustomerPwd.setPasswordCreatedBy(userName);

        when(userInfoRepository.findUserNameByDigitalCustomerProfileId(digitalProfileIdString)).thenReturn(userName);
        when(digitalCustomerPwdRepository.save(any(DigitalCustomerPwd.class))).thenReturn(digitalCustomerPwd);

        // Act
        DigitalCustomerPwdResponse response = userInfoService.storeOldPassword(request);

        // Assert
        assertEquals(digitalProfileIdString, response.getDigitalProfileId());
        assertEquals("Old password stored successfully.", response.getMessage());
        verify(digitalCustomerPwdRepository, times(1)).save(any(DigitalCustomerPwd.class));
        verify(userInfoRepository, times(1))
                .findUserNameByDigitalCustomerProfileId(digitalProfileIdString);
    }

    @Test
    void testStoreOldPasswordException() {
        // Arrange
        UUID digitalProfileIdString = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");
        String password = "myOldPassword";
        String userName = "testUser";

        DigitalPwdRequest request = DigitalPwdRequest.builder()
                .digitalProfileId(digitalProfileIdString)
                .password(password)
                .build();

        when(userInfoRepository.findUserNameByDigitalCustomerProfileId(digitalProfileIdString)).thenReturn(userName);
        when(digitalCustomerPwdRepository.save(any(DigitalCustomerPwd.class)))
                .thenThrow(new RuntimeException("Error saving old password"));

        // Act and Assert
        DigitalPasswordStorageException exception = assertThrows(DigitalPasswordStorageException.class, () -> {
            userInfoService.storeOldPassword(request);
        });

        assertEquals("Error storing old password.", exception.getMessage());
        verify(digitalCustomerPwdRepository, times(1)).save(any(DigitalCustomerPwd.class));
        verify(userInfoRepository, times(1))
                .findUserNameByDigitalCustomerProfileId(digitalProfileIdString);
    }

    @Test
    void testGetAllRegisterDeviceValidProfileIdRegistered() {
        UUID digitalCustomerProfileId1 = UUID.randomUUID();
        boolean registered = true;
        List<Object[]> mockDevices = new ArrayList<>();
        final int defaultDeviceId = 42;
        Object[] mockDetail = new Object[]{
                digitalCustomerProfileId1.toString(),
                defaultDeviceId,
                "15 Pro",
                Timestamp.valueOf("2024-03-27 13:30:22.58"),
                Timestamp.valueOf("2024-09-13 14:26:25.500"),
                true,
                UUID.randomUUID().toString()
        };
        mockDevices.add(mockDetail);
        when(digitalCustomerProfileRepository.existsByDigitalCustomerProfileId(digitalCustomerProfileId1))
                .thenReturn(true);
        when(digitalCustomerDeviceRepository.findRegisteredDevicesByDigitalCustomerProfileIdAndDeviceStatus(
                digitalCustomerProfileId1))
                .thenReturn(mockDevices);

        List<DeviceDataForRegisterDevice> result = userInfoService.getAllRegisterDevice(
                digitalCustomerProfileId1, registered);

        assertEquals(mockDevices.size(), result.size());

    }

    @Test
    void testUpdateBiometricStatus() {
        // Arrange
        BiometricStatusDTO request = new BiometricStatusDTO();
        request.setFaceId(true);
        request.setTouchId(true);
        UUID digitalCustomerProfileId1 = UUID.fromString("8869a9ba-5240-44bf-9491-0e6798581f49");

        // Act
        String response = userInfoService.updateBiometricStatus(request, digitalCustomerProfileId1);

        // Assert
        assertEquals("Biometric status updated successfully", response);
    }

    @Test
    void testDeRegisterDevicesNoDevices() {
        UUID digitalCustomerProfileId1 = UUID.randomUUID();
        List<String> devicesUdidList = Collections.singletonList("device1");

        when(digitalCustomerDeviceRepository.getListOfDeviceCustomerIds(devicesUdidList))
                .thenReturn(Collections.emptyList());

        assertThrows(DigitalCustomerDeviceNotFoundException.class, () -> {
            userInfoService.deRegisterDevices(digitalCustomerProfileId1, devicesUdidList);
        });
    }

    @Test
    void testDeRegisterDevices1() {
        // Arrange
        UUID digitalCustomerProfileId1 = UUID.randomUUID();
        List<String> devicesUdidList = Arrays.asList("58c699b4-6f73-47c4-ae7b-4fdcf0fb4939");
        List<Integer> customerDeviceIds = Arrays.asList(1);
        List<Integer> registeredCustomerDevicesIds = Arrays.asList(1);

        // Mock the dependencies
        when(digitalCustomerDeviceRepository.getListOfDeviceCustomerIds(devicesUdidList)).thenReturn(customerDeviceIds);
        when(digitalDeviceLinkRepository.getRegisteredCustomerDevicesIds(digitalCustomerProfileId1))
                .thenReturn(registeredCustomerDevicesIds);
        when(digitalDeviceLinkRepository.deRegisteredDevices(customerDeviceIds)).thenReturn(1);
        when(digitalCustomerDeviceRepository.updateBiometricPublicKeyNull(customerDeviceIds)).thenReturn(1);

        // Act
        DeRegisterDevicesResponse result = userInfoService.deRegisterDevices(
                digitalCustomerProfileId1, devicesUdidList);

        // Assert
        assertEquals(OK_RESPONSE_CODE, result.getStatusCode());
        assertEquals("De-Registered Successfully", result.getMessage());
    }

    @Test
    void testDeRegisterDevicesDatabaseOperationsException() {
        // Arrange
        UUID digitalCustomerProfileId1 = UUID.randomUUID();
        List<String> devicesUdidList = Arrays.asList("device1", "device2");

        // Mock the behavior of the repositories to simulate a scenario that triggers the exception
        when(digitalCustomerDeviceRepository.getListOfDeviceCustomerIds(any())).thenReturn(Arrays.asList(1, 2));
        when(digitalDeviceLinkRepository.getRegisteredCustomerDevicesIds(any())).thenReturn(Arrays.asList(1, 2));
        doThrow(RuntimeException.class).when(digitalDeviceLinkRepository).deRegisteredDevices(any());

        // Act
        Exception exception = assertThrows(DatabaseOperationsException.class, () -> {
            userInfoService.deRegisterDevices(digitalCustomerProfileId1, devicesUdidList);
        });

        // Assert
        assertEquals("Unable to update the registered flag", exception.getMessage());
    }

    @Test
    void testGetBroadCastNonEmptyReferenceId() {
        // Arrange
        String digitalProfileIdString = "123e4567-e89b-12d3-a456-426655440000";
        UUID digitalProfileId = UUID.fromString(digitalProfileIdString);
        List<String> broadCastReferenceList = new ArrayList<>();
        broadCastReferenceList.add("1");

        when(userInfoRepository.getBroadCastReferenceId(digitalProfileId)).thenReturn(broadCastReferenceList);
        List<String> referenceIdList = userInfoService.getBroadCastReferenceId(digitalProfileId);

        assertNotNull(referenceIdList);
    }

    @Test
    void testGetBroadCastEmptyReferenceId() {
        // Arrange
        String digitalProfileIdString = "123e4567-e89b-12d3-a456-426655440000";
        UUID digitalProfileId = UUID.fromString(digitalProfileIdString);


        when(userInfoRepository.getBroadCastReferenceId(digitalProfileId)).thenReturn(null);
        List<String> referenceId = userInfoService.getBroadCastReferenceId(digitalProfileId);

        assertTrue(referenceId.isEmpty());
    }

    private UserInfoServiceImpl userInfoServiceImpl() {
        return new UserInfoServiceImpl(digitalCustomerDeviceRepository,
                digitalDeviceLinkRepository, loginAttemptRepository,
                mock(String.valueOf(DigitalCustomerPwdRepository.class)), new AppConfig(),
                mock(String.valueOf(UserInfoRepository.class)),
                mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                mock(String.valueOf(DigitalCustomerAlertService.class)),
                mock(String.valueOf(NotificationOrchestratorServiceClient.class)),
                digitalCustomerDeviceAuditRepository,
                mock(String.valueOf(ConfigurationServiceClient.class)), pinRepository,
                mock(String.valueOf(NotificationUtil.class)));
    }

    @Test
    void testUpdateExpirySuccessForPassword() {
        // Arrange
        UpdateExpiryDTO updateExpiryDTO = new UpdateExpiryDTO("username", "password");
        DigitalCustomerProfile profile = mock(DigitalCustomerProfile.class);
        when(profile.getDigitalUserName()).thenReturn(updateExpiryDTO.getUsername());

        when(userInfoRepository.findByDigitalUserName(updateExpiryDTO.getUsername())).thenReturn(profile);
        when(configurationServiceClient.getGlobalConfig(UdbConstants.UDB_PWD_EXPRY_PERIOD))
                .thenReturn(List.of(new GlobalConfigResponse("30", "default_value")));

        // Act
        UpdateExpiryResponse response = userInfoService.updateExpiry(updateExpiryDTO);

        // Assert
        assertEquals(updateExpiryDTO.getUsername(), response.getUsername());
        assertEquals(PASSWORD_EXPIRY_DATE_UPDATED_SUCCESSFULLY, response.getMessage());
        verify(profile).setPwdExpiryDate(any(LocalDateTime.class));
        verify(digitalCustomerProfileRepository).save(profile);
    }

    @Test
    void testUpdateExpirySuccessForPin() {
        // Arrange
        UpdateExpiryDTO updateExpiryDTO = new UpdateExpiryDTO("username", "pin");
        DigitalCustomerProfile profile = mock(DigitalCustomerProfile.class);
        when(profile.getDigitalUserName()).thenReturn(updateExpiryDTO.getUsername());

        when(userInfoRepository.findByDigitalUserName(updateExpiryDTO.getUsername())).thenReturn(profile);
        when(configurationServiceClient.getGlobalConfig(UdbConstants.UDB_PIN_EXPRY_PERIOD))
                .thenReturn(List.of(new GlobalConfigResponse("90", "default_value")));

        // Act
        UpdateExpiryResponse response = userInfoService.updateExpiry(updateExpiryDTO);

        // Assert
        assertEquals(updateExpiryDTO.getUsername(), response.getUsername());
        assertEquals(PIN_EXPIRY_DATE_UPDATED_SUCCESSFULLY, response.getMessage());
        verify(profile).setPinExpiryDate(any(LocalDateTime.class));
        verify(digitalCustomerProfileRepository).save(profile);
    }

    @Test
    void testUpdateExpirySuccessForPinWithUnlockAccount() {
        // Arrange
        UpdateExpiryDTO updateExpiryDTO = new UpdateExpiryDTO("username", "pin");
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalCustomerStatusTypeId(UdbConstants.UNLOCK_PENDING);
        profile.setDigitalUserName(updateExpiryDTO.getUsername());

        when(userInfoRepository.findByDigitalUserName(updateExpiryDTO.getUsername())).thenReturn(profile);
        when(configurationServiceClient.getGlobalConfig(UdbConstants.UDB_PIN_EXPRY_PERIOD))
                .thenReturn(List.of(new GlobalConfigResponse("90", "default_value")));

        // Act
        UpdateExpiryResponse response = userInfoService.updateExpiry(updateExpiryDTO);

        // Assert
        assertEquals(updateExpiryDTO.getUsername(), response.getUsername());
        assertEquals(PIN_EXPIRY_DATE_UPDATED_SUCCESSFULLY, response.getMessage());
        verify(digitalCustomerProfileRepository).save(profile);
    }

    @Test
    void testUpdateExpiryUserNotFound() {
        // Arrange
        UpdateExpiryDTO updateExpiryDTO = new UpdateExpiryDTO("username", "password");
        when(userInfoRepository.findByDigitalUserName(updateExpiryDTO.getUsername())).thenReturn(null);
        when(configurationServiceClient.getGlobalConfig(any())).thenReturn(List.of(new GlobalConfigResponse("30", "")));

        // Act and Assert
        assertThrows(UserNameNotFoundException.class, () -> userInfoService.updateExpiry(updateExpiryDTO));
    }

    @Test
    void testUpdateExpiryInvalidUpdateType() {
        // Arrange
        UpdateExpiryDTO updateExpiryDTO = new UpdateExpiryDTO("username", "invalid");
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        profile.setDigitalUserName(updateExpiryDTO.getUsername());
        when(userInfoRepository.findByDigitalUserName(updateExpiryDTO.getUsername())).thenReturn(profile);
        when(configurationServiceClient.getGlobalConfig(any())).thenReturn(List.of(
                new GlobalConfigResponse("30", "default_value")));

        // Act and Assert
        assertThrows(InvalidDataException.class, () -> userInfoService.updateExpiry(updateExpiryDTO));
    }

    @Test
    void testUpdateExpiryExceptionDuringSave() {
        UpdateExpiryDTO updateExpiryDTO = new UpdateExpiryDTO("testUser", "password");
        DigitalCustomerProfile profile = new DigitalCustomerProfile();
        when(userInfoRepository.findByDigitalUserName(updateExpiryDTO.getUsername())).thenReturn(profile);
        when(configurationServiceClient.getGlobalConfig(UdbConstants.UDB_PWD_EXPRY_PERIOD))
                .thenReturn(List.of(new GlobalConfigResponse("30", "default_value")));
        doThrow(new RuntimeException("Database error")).when(digitalCustomerProfileRepository).save(profile);

        assertThrows(PasswordExpiryException.class, () -> {
            userInfoService.updateExpiry(updateExpiryDTO);
        });
    }

    @Test
    void testHandleWebClientExceptionServiceUnavailable() throws Throwable {
        WebClientResponseException webClientException = mock(WebClientResponseException.class);
        when(webClientException.getMessage()).thenReturn(UdbConstants.SERVICE_UNAVAILABLE);

        // Act & Assert
        assertThrows(ConfigurationServiceUnavailableException.class, () ->
                invokeHandleWebClientException(webClientException));
    }

    @Test
    void testHandleWebClientExceptionInternalServerError() throws Throwable {
        WebClientResponseException webClientException = mock(WebClientResponseException.class);
        when(webClientException.getMessage()).thenReturn(UdbConstants.INTERNAL_SERVER_ERROR_CODE);

        // Act & Assert
        assertThrows(ConfigurationServiceException.class, () -> invokeHandleWebClientException(webClientException));
    }

    @Test
    void testHandleWebClientExceptionOtherException() throws Throwable {
        // Arrange
        WebClientResponseException webClientException = mock(WebClientResponseException.class);
        when(webClientException.getMessage()).thenReturn("OTHER_ERROR");

        // Create a mock UdbExceptionResponse
        UdbExceptionResponse mockResponse = mock(UdbExceptionResponse.class);
        when(webClientException.getResponseBodyAs(UdbExceptionResponse.class)).thenReturn(mockResponse);

        when(webClientException.getStatusCode()).thenReturn(HttpStatus.BAD_GATEWAY);

        // Act & Assert
        assertThrows(WebClientIntegrationException.class, () -> invokeHandleWebClientException(webClientException));
    }

    // Helper method to handle exception invocation
    private void invokeHandleWebClientException(WebClientResponseException exception) throws Throwable {
        try {
            handleWebClientExceptionMethod.invoke(userInfoService, exception);
        } catch (InvocationTargetException e) {
            throw e.getCause(); // Ensure the original cause is thrown
        }
    }

    @Test
    void testGetGlobalConfigValueWebClientResponseException() {
        // Arrange
        WebClientResponseException webClientException = mock(WebClientResponseException.class);
        when(webClientException.getMessage()).thenReturn(UdbConstants.SERVICE_UNAVAILABLE);
        when(configurationServiceClient.getGlobalConfig(UdbConstants.UDB_PWD_EXPRY_PERIOD))
                .thenThrow(webClientException);

        // Act & Assert
        assertThrows(ConfigurationServiceUnavailableException.class, () -> {
            userInfoService.getGlobalConfigValue("password");
        });

        // Verify
        verify(configurationServiceClient).getGlobalConfig(UdbConstants.UDB_PWD_EXPRY_PERIOD);
    }

    @Test
    void testLockUserAccountSuccess() {
        when(digitalCustomerProfileRepository.lockUserAccount(UUID.randomUUID())).thenReturn(1);
        doNothing().when(notificationUtil).sendNotification(anyMap(), anyMap());

        UserLockResponse response = userInfoService.lockUserAccount(testUUID);

        assertEquals(USER_LOCKED_SUCCESS_MESSAGE, response.getMessage());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        verify(digitalCustomerProfileRepository).lockUserAccount(testUUID);
        verify(notificationUtil).sendNotification(anyMap(), anyMap());
    }

    @Test
    void testLockUserAccountDataAccessException() {
        when(digitalCustomerProfileRepository.lockUserAccount(testUUID)).thenThrow(
                new DataAccessException("Database error") {
                });

        UserLockResponse response = userInfoService.lockUserAccount(testUUID);

        assertEquals(DATABASE_ERROR_MESSAGE, response.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode());
        verify(digitalCustomerProfileRepository).lockUserAccount(testUUID);
        verify(notificationUtil, never()).sendNotification(anyMap(), anyMap());
    }

    @Test
    void testLockUserAccountUnexpectedException() {
        when(digitalCustomerProfileRepository.lockUserAccount(testUUID)).thenThrow(
                new RuntimeException("Unexpected error"));

        UserLockResponse response = userInfoService.lockUserAccount(testUUID);

        assertEquals(UNEXPECTED_ERROR_MESSAGE, response.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode());
        verify(digitalCustomerProfileRepository).lockUserAccount(testUUID);
        verify(notificationUtil, never()).sendNotification(anyMap(), anyMap());
    }

    @Test
    void testLockUserAccountNoRowsUpdated() {
        when(digitalCustomerProfileRepository.lockUserAccount(testUUID)).thenReturn(0);

        UserLockResponse response = userInfoService.lockUserAccount(testUUID);

        assertEquals(USER_LOCKED_SUCCESS_MESSAGE, response.getMessage());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        verify(digitalCustomerProfileRepository).lockUserAccount(testUUID);
        verify(notificationUtil).sendNotification(anyMap(), anyMap());
    }
}
