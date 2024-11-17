package com.unisys.udb.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.unisys.udb.user.config.AppConfig;
import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.BankingNotificationPreferenceRequest;
import com.unisys.udb.user.dto.request.BiometricStatusDTO;
import com.unisys.udb.user.dto.request.DigitalCookiesPreferenceRequest;
import com.unisys.udb.user.dto.request.DigitalCustomerProfileDTO;
import com.unisys.udb.user.dto.request.DigitalCustomerShortcutsRequest;
import com.unisys.udb.user.dto.request.MarketingNotificationPreferenceRequest;
import com.unisys.udb.user.dto.request.PinChangeRequest;
import com.unisys.udb.user.dto.request.PublicKeyUpdateRequest;
import com.unisys.udb.user.dto.request.SessionHistoryFilterRequest;
import com.unisys.udb.user.dto.request.TermsConditionsAndCookiesRequest;
import com.unisys.udb.user.dto.request.UserDetailDto;
import com.unisys.udb.user.dto.request.UserPublicKeyRequest;
import com.unisys.udb.user.dto.response.BankingNotificationPreferenceResponse;
import com.unisys.udb.user.dto.response.BiometricPublicKeyResponse;
import com.unisys.udb.user.dto.response.BiometricStatusResponse;
import com.unisys.udb.user.dto.response.CheckPinStatusResponse;
import com.unisys.udb.user.dto.response.CoreCustomerProfileResponse;
import com.unisys.udb.user.dto.response.CustomerSessionHistoryResponse;
import com.unisys.udb.user.dto.response.DeRegisterDevicesResponse;
import com.unisys.udb.user.dto.response.DeviceDataForRegisterDevice;
import com.unisys.udb.user.dto.response.DeviceInfoResponse;
import com.unisys.udb.user.dto.response.DigitalCookiePreferenceResponse;
import com.unisys.udb.user.dto.response.DigitalCookiesPreferenceResponse;
import com.unisys.udb.user.dto.response.DigitalCustomerShortcutsResponse;
import com.unisys.udb.user.dto.response.GetTermsConditionAndCookiesInfoResponse;
import com.unisys.udb.user.dto.response.MarketingNotificationPreferenceResponse;
import com.unisys.udb.user.dto.response.MarketingPreferenceResponse;
import com.unisys.udb.user.dto.response.NotificationPreferenceResponse;
import com.unisys.udb.user.dto.response.PinChangeResponse;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.dto.response.UserInfoResponse;
import com.unisys.udb.user.dto.response.UserNameResponse;
import com.unisys.udb.user.dto.response.UserStatusResponse;
import com.unisys.udb.user.dto.response.UserSuccessResponse;
import com.unisys.udb.user.entity.CountryValidation;
import com.unisys.udb.user.entity.CustomerSessionHistory;
import com.unisys.udb.user.entity.DigitalCustomerDevice;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import com.unisys.udb.user.entity.DigitalMarketingNotificationPreference;
import com.unisys.udb.user.entity.DigitalNotificationPreference;
import com.unisys.udb.user.exception.DeviceIdParamNotFoundException;
import com.unisys.udb.user.exception.DigitalCookiePreferenceUpdateException;
import com.unisys.udb.user.exception.DigitalCustomerProfileIdNotFoundException;
import com.unisys.udb.user.exception.DigitalCustomerProfileIdNotNullException;
import com.unisys.udb.user.exception.DigitalCustomerShortcutRequestNotFound;
import com.unisys.udb.user.exception.DigitalCustomerShortcutUpdateException;
import com.unisys.udb.user.exception.InvalidRequestException;
import com.unisys.udb.user.repository.BankingNotificationPreferenceRepository;
import com.unisys.udb.user.repository.DigitalCookiePreferenceRepository;
import com.unisys.udb.user.repository.DigitalCustomerAlertRepository;
import com.unisys.udb.user.repository.DigitalCustomerDeviceAuditRepository;
import com.unisys.udb.user.repository.DigitalCustomerDeviceRepository;
import com.unisys.udb.user.repository.DigitalCustomerProfileRepository;
import com.unisys.udb.user.repository.DigitalCustomerPwdRepository;
import com.unisys.udb.user.repository.DigitalCustomerShortcutsRepository;
import com.unisys.udb.user.repository.DigitalDeviceLinkRepository;
import com.unisys.udb.user.repository.DigitalDocdbAlertRefRepository;
import com.unisys.udb.user.repository.LoginAttemptRepository;
import com.unisys.udb.user.repository.MarketingNotificationPreferencesRepository;
import com.unisys.udb.user.repository.PinRepository;
import com.unisys.udb.user.repository.SesionHistoryRepository;
import com.unisys.udb.user.repository.UserInfoRepository;
import com.unisys.udb.user.service.DigitalCookiesPreferenceService;
import com.unisys.udb.user.service.DigitalCustomerAlertService;
import com.unisys.udb.user.service.DigitalCustomerDeviceService;
import com.unisys.udb.user.service.DigitalCustomerProfileAndDeviceInjector;
import com.unisys.udb.user.service.DigitalCustomerShortcutsService;
import com.unisys.udb.user.service.DigitalCustomerShortcutsServiceImpl;
import com.unisys.udb.user.service.PinService;
import com.unisys.udb.user.service.PinServiceImpl;
import com.unisys.udb.user.service.PromotionOffers;
import com.unisys.udb.user.service.UserInfoService;
import com.unisys.udb.user.service.UserRegistrationService;
import com.unisys.udb.user.service.UserRegistrationServiceImpl;
import com.unisys.udb.user.service.client.ConfigurationServiceClient;
import com.unisys.udb.user.service.client.NotificationOrchestratorServiceClient;
import com.unisys.udb.user.service.impl.DigitalCustomerAlertServiceImpl;
import com.unisys.udb.user.service.impl.DigitalCustomerDeviceServiceImpl;
import com.unisys.udb.user.service.impl.PinHistoryServiceImpl;
import com.unisys.udb.user.service.impl.UserInfoServiceImpl;
import com.unisys.udb.user.utils.JsonUtils;
import com.unisys.udb.user.utils.dto.response.NotificationUtil;
import com.unisys.udb.utility.auditing.dto.AuditDigitalCustomerHolder;
import com.unisys.udb.utility.auditing.dto.CustomerActionAuditHolder;
import com.unisys.udb.utility.linkmessages.dto.DynamicMessageResponse;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static com.unisys.udb.user.constants.UdbConstants.DEVICE_ID1;
import static com.unisys.udb.user.constants.UdbConstants.FAILURE;
import static com.unisys.udb.user.constants.UdbConstants.NOT_ACCEPTABLE;
import static com.unisys.udb.user.constants.UdbConstants.NOT_FOUND_ERROR_CODE;
import static com.unisys.udb.user.constants.UdbConstants.NOT_FOUND_ERROR_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.OK_RESPONSE_CODE;
import static com.unisys.udb.user.constants.UdbConstants.SHORTCUTS_NOT_ACCEPTABLE_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.SUCCESS_CODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {UserController.class})
@ExtendWith(SpringExtension.class)
class UserControllerTest {
    public static final int DIGITAL_BANKING_NOTIFICATION_PREFERENCE_ID = 42;
    private static final int YEAR_1970 = 1970;
    private final int badRequest = 400;
    private final int okStatus = 200;
    private final long num = 10;
    private final Integer numInt = 10;
    private final int number = 200;
    @Autowired
    private UserController userController;
    @MockBean
    private UserRegistrationServiceImpl userRegistrationService;
    @MockBean
    private PinService pinService;
    @MockBean
    private UserInfoService userInfoService;
    @MockBean
    private DigitalCustomerShortcutsService digitalCustomerShortcutsService;
    @MockBean
    private DigitalCookiesPreferenceService digitalCookiesPreferenceService;
    @MockBean
    private AuditDigitalCustomerHolder auditDigitalCustomerIdHolder;
    @MockBean
    private PromotionOffers promotionOffers;
    @MockBean
    private UserInfoRepository userInfoRepository;
    @MockBean
    private PinHistoryServiceImpl pinHistoryServiceImpl;
    @MockBean
    private DigitalCustomerDeviceService digitalDeviceService;

    @MockBean
    private CustomerActionAuditHolder customerActionAuditHolder;
    @MockBean
    private JsonUtils jsonUtils;

    @MockBean
    private PinRepository pinRepository;

    @Mock
    private DigitalCustomerDeviceRepository digitalCustomerDeviceRepository;

    @Mock
    private DigitalDeviceLinkRepository digitalDeviceLinkRepository;
    @Mock
    private LoginAttemptRepository loginAttemptRepository;



    @Test
    void testRegisterUserAndDeviceInfo() {
        UserRegistrationServiceImpl userRegistrationService = mock(UserRegistrationServiceImpl.class);
        DynamicMessageResponse.Message message = new DynamicMessageResponse.Message();
        List<DynamicMessageResponse.Message> messages = new ArrayList<>();
        messages.add(message);
        DynamicMessageResponse dynamicMessageResponse = new DynamicMessageResponse(
                "Success", "myCustomCode", messages
        );
        Mono<DynamicMessageResponse> justResult = Mono.just(dynamicMessageResponse);
        when(userRegistrationService.saveUserAndDeviceInfo(Mockito.any()))
                .thenReturn(justResult);

        UserController userController = new UserController(userRegistrationService, userInfoService, pinService,
                digitalCustomerShortcutsService, digitalCookiesPreferenceService, auditDigitalCustomerIdHolder,
                promotionOffers, digitalDeviceService,
                jsonUtils);
        Mono<ResponseEntity<DynamicMessageResponse>> actualRegisterUserAndDeviceInfoResult = userController
                .registerUserAndDeviceInfo(new DigitalCustomerProfileDTO("42", "janedoe", "42", "Digital Device Udid",
                        "Device Name", "Device Type", "1.0.2", ""));
        verify(userRegistrationService).saveUserAndDeviceInfo(Mockito.any());
        StepVerifier.create(actualRegisterUserAndDeviceInfoResult)
                .expectNextMatches(responseEntity -> {
                    if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                        return false;
                    }
                    DynamicMessageResponse responseBody = responseEntity.getBody();
                    if (responseBody == null) {
                        return false;
                    }
                    if (!"Success".equals(responseBody.getResponseType())) {
                        return false;
                    }
                    if (!"myCustomCode".equals(responseBody.getStatusMessage())) {
                        return false;
                    }
                    if (responseBody.getMessages().size() != 1) {
                        return false;
                    }
                    DynamicMessageResponse.Message responseMessage = responseBody.getMessages().get(0);
                    return responseMessage.equals(message);
                })
                .verifyComplete();
    }

    void testFetchUserInfo() {
        UserRegistrationServiceImpl userRegistrationService = mock(UserRegistrationServiceImpl.class);
        CoreCustomerProfileResponse buildResult = CoreCustomerProfileResponse.builder()
                .email("abc.bdc@example.org")
                .firstName("abc")
                .lastName("bdc")
                .middleName("Middle Name")
                .phone("6625550144")
                .build();
        Mono<CoreCustomerProfileResponse> justResult = Mono.just(buildResult);
        when(userRegistrationService.fetchUserInfo(Mockito.any())).thenReturn(justResult);
        userController.fetchUserInfo(UUID.randomUUID());
        verify(userRegistrationService).fetchUserInfo(Mockito.any());
    }

    @Test
    void testFetchUserInfo2() {
        Mono<CoreCustomerProfileResponse> mono = mock(Mono.class);
        Mono<Object> justResult = Mono.just("Data");
        when(mono.map(Mockito.<Function<CoreCustomerProfileResponse, Object>>any())).thenReturn(justResult);
        UserRegistrationServiceImpl userRegistrationService = mock(UserRegistrationServiceImpl.class);
        when(userRegistrationService.fetchUserInfo(Mockito.any())).thenReturn(mono);
        UserController userController = new UserController(userRegistrationService, userInfoService, pinService,
                digitalCustomerShortcutsService, digitalCookiesPreferenceService, auditDigitalCustomerIdHolder,
                promotionOffers, digitalDeviceService,
                jsonUtils);
        Mono<ResponseEntity<CoreCustomerProfileResponse>> actualFetchUserInfoResult = userController
                .fetchUserInfo(UUID.randomUUID());
        verify(userRegistrationService).fetchUserInfo(Mockito.any());
        verify(mono).map(Mockito.<Function<CoreCustomerProfileResponse, Object>>any());
        assertSame(justResult, actualFetchUserInfoResult);
    }

    @Test
    void getDigitalCustomerProfileTest() {
        String deviceUdid = "3F2504E0-4F89-41D3-9A0C-0305E62C000";
        UserInfoResponse userInfoResponse = UserInfoResponse.builder().build();
        UserRegistrationServiceImpl userRegistrationService = mock(UserRegistrationServiceImpl.class);
        when(userInfoService.getUserInfoResponse(deviceUdid)).thenReturn(userInfoResponse);
        UserController userController = new UserController(userRegistrationService, userInfoService, pinService,
                digitalCustomerShortcutsService, digitalCookiesPreferenceService, auditDigitalCustomerIdHolder,
                promotionOffers, digitalDeviceService,
                jsonUtils);
        Mono<ResponseEntity<UserInfoResponse>> responseEntityMono = userController.getDigitalCustomerProfile(
                deviceUdid);
        Assertions.assertNotNull(responseEntityMono);
    }


    @Test
    void testUpdateDigitalCustomerShortcutRequestNotFound() {
        UserInfoRepository customerRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        DigitalCookiePreferenceRepository digitalCookiePreferenceRepository =
                mock(DigitalCookiePreferenceRepository.class);
        UserController userController = new UserController(
                new UserRegistrationServiceImpl(customerRepository, digitalCustomerProfileRepository,
                        bankingNotificationPreferenceRepository, marketingNotificationPreferencesRepository,
                        new DigitalCustomerProfileAndDeviceInjector(
                                mock(String.valueOf(userInfoServiceImpl())),
                                mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                                mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                                mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                                mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                                mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                        digitalCookiePreferenceRepository, mock(DigitalCustomerDeviceRepository.class),
                        mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                        mock(NotificationUtil.class)),
                mock(UserInfoService.class), mock(PinService.class), mock(DigitalCustomerShortcutsService.class),
                mock(DigitalCookiesPreferenceService.class), auditDigitalCustomerIdHolder,
                promotionOffers, digitalDeviceService,
                jsonUtils);
        UUID digitalCustomerProfileId = UUID.randomUUID();
        String digitalCustomerName = "Customer";
        assertThrows(DigitalCustomerShortcutUpdateException.class, () -> {
            userController.updateDigitalCustomerShortcut(digitalCustomerProfileId, null, digitalCustomerName);
        });
    }

    @Test
    void updateDigitalCustomerShortcutSuccess() throws DigitalCustomerShortcutUpdateException, JsonProcessingException {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        String digitalCustomerName = "Customer";
        DigitalCustomerShortcutsRequest request = new DigitalCustomerShortcutsRequest();
        request.setFundTransferShortcut(true);
        request.setEstatementShortcut(false);
        request.setPayeeShortcut(true);
        request.setScheduledPaymentsShortcut(false);
        request.setCommPrefShortcut(true);
        request.setSessionHistoryShortcut(false);
        UserAPIBaseResponse serviceResponse = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .status("Successful!")
                .message("Digital Customer Shortcuts Updated Successfully!")
                .build();
        when(digitalCustomerShortcutsService.updateDigitalCustomerShortcut(digitalCustomerProfileId, request))
                .thenReturn(Mono.just(serviceResponse));
        ResponseEntity<UserAPIBaseResponse> responseEntity = userController
                .updateDigitalCustomerShortcut(digitalCustomerProfileId, request, digitalCustomerName)
                .block();
        Assertions.assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        assertEquals("Successful!", responseEntity.getBody().getStatus());
        assertEquals("Digital Customer Shortcuts Updated Successfully!", responseEntity.getBody().getMessage());
    }

    @Test
    void testUpdateDigitalCustomerShortcut3() throws DigitalCustomerShortcutUpdateException, JsonProcessingException {
        Mono<UserAPIBaseResponse> mono = mock(Mono.class);
        String digitalCustomerName = "Customer";
        UserAPIBaseResponse buildResult = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.CONTINUE)
                .message("Not all who wander are lost")
                .status("Status")
                .build();
        when(mono.block()).thenReturn(buildResult);
        DigitalCustomerShortcutsService digitalCustomerShortcutsService =
                mock(DigitalCustomerShortcutsService.class);
        when(digitalCustomerShortcutsService.updateDigitalCustomerShortcut(
                Mockito.any(), Mockito.any())).thenReturn(mono);
        UserInfoRepository customerRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        DigitalCookiePreferenceRepository digitalCookiePreferenceRepository =
                mock(DigitalCookiePreferenceRepository.class);
        UserController userController = new UserController(
                new UserRegistrationServiceImpl(customerRepository, digitalCustomerProfileRepository,
                        bankingNotificationPreferenceRepository, marketingNotificationPreferencesRepository,
                        new DigitalCustomerProfileAndDeviceInjector(
                                mock(String.valueOf(userInfoServiceImpl())),
                                mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                                mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                                mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                                mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                                mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                        digitalCookiePreferenceRepository, mock(DigitalCustomerDeviceRepository.class),
                        mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                        mock(NotificationUtil.class)),
                mock(UserInfoService.class), pinService, digitalCustomerShortcutsService,
                digitalCookiesPreferenceService,
                auditDigitalCustomerIdHolder, promotionOffers, digitalDeviceService,
                jsonUtils);
        UUID digitalCustomerProfileId = UUID.randomUUID();
        userController.updateDigitalCustomerShortcut(digitalCustomerProfileId,
                new DigitalCustomerShortcutsRequest(true,
                        true, true, true, true, true), digitalCustomerName);
        verify(digitalCustomerShortcutsService).updateDigitalCustomerShortcut(Mockito.any(), Mockito.any());
        verify(mono).block();
    }

    @Test
    void testUpdateDigitalCustomerShortcutForAtLeastThreeTrue() throws DigitalCustomerShortcutUpdateException,
            JsonProcessingException {
        Mono<UserAPIBaseResponse> mono = mock(Mono.class);
        UserAPIBaseResponse buildResult = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.CONTINUE)
                .message("Not all who wander are lost")
                .status("Status")
                .build();
        when(mono.block()).thenReturn(buildResult);
        String digitalCustomerName = "Customer";
        DigitalCustomerShortcutsService digitalCustomerShortcutsService =
                mock(DigitalCustomerShortcutsService.class);
        when(digitalCustomerShortcutsService.updateDigitalCustomerShortcut(Mockito.any(),
                Mockito.any())).thenReturn(mono);
        UserInfoRepository customerRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        DigitalCookiePreferenceRepository digitalCookiePreferenceRepository =
                mock(DigitalCookiePreferenceRepository.class);
        UserController userController = new UserController(
                new UserRegistrationServiceImpl(customerRepository, digitalCustomerProfileRepository,
                        bankingNotificationPreferenceRepository, marketingNotificationPreferencesRepository,
                        new DigitalCustomerProfileAndDeviceInjector(
                                mock(String.valueOf(userInfoServiceImpl())),
                                mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                                mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                                mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                                mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                                mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                        digitalCookiePreferenceRepository, mock(DigitalCustomerDeviceRepository.class),
                        mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                        mock(NotificationUtil.class)),
                mock(UserInfoService.class), pinService, digitalCustomerShortcutsService,
                digitalCookiesPreferenceService,
                auditDigitalCustomerIdHolder, promotionOffers, digitalDeviceService,
                jsonUtils);
        UUID digitalCustomerProfileId = UUID.randomUUID();
        userController.updateDigitalCustomerShortcut(digitalCustomerProfileId,
                new DigitalCustomerShortcutsRequest(false, true, true,
                        true, true, true), digitalCustomerName);
        verify(digitalCustomerShortcutsService).updateDigitalCustomerShortcut(Mockito.any(), Mockito.any());
        verify(mono).block();
    }

    @Test
    void testJsonProcessingException() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        String digitalCustomerName = "Customer";
        DigitalCustomerShortcutsRequest validRequest = null;
        assertThrows(DigitalCustomerShortcutUpdateException.class, () -> {
            userController.updateDigitalCustomerShortcut(digitalCustomerProfileId, validRequest,
                    digitalCustomerName).block();
        });
    }

    @Test
    void testFieldsAsFalse() {
        String digitalCustomerName = "Customer";
        DigitalCustomerShortcutsRequest digitalCustomerShortcutsRequest =
                new DigitalCustomerShortcutsRequest();
        digitalCustomerShortcutsRequest.setCommPrefShortcut(true);
        digitalCustomerShortcutsRequest.setEstatementShortcut(true);
        digitalCustomerShortcutsRequest.setFundTransferShortcut(false);
        digitalCustomerShortcutsRequest.setPayeeShortcut(false);
        digitalCustomerShortcutsRequest.setScheduledPaymentsShortcut(false);
        digitalCustomerShortcutsRequest.setSessionHistoryShortcut(false);
        try {
            userController.updateDigitalCustomerShortcut(UUID.randomUUID(),
                    digitalCustomerShortcutsRequest, digitalCustomerName);
        } catch (DigitalCustomerShortcutRequestNotFound | DigitalCustomerShortcutUpdateException e) {
            List<String> errorCode = new ArrayList<>();
            errorCode.add(NOT_ACCEPTABLE);
            //"At least three shortcuts should be enabled", e.getMessage()
            assertEquals(new DigitalCustomerShortcutRequestNotFound(errorCode,
                    HttpStatus.NOT_ACCEPTABLE,
                    FAILURE,
                    SHORTCUTS_NOT_ACCEPTABLE_MESSAGE,
                    new ArrayList<>()).getMessage(), e.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testGetBankingPreference() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        BankingNotificationPreferenceResponse response = new BankingNotificationPreferenceResponse();
        when(userRegistrationService.getBankingPreference(any(UUID.class)))
                .thenReturn(Mono.just(response));

        // Act
        Mono<ResponseEntity<BankingNotificationPreferenceResponse>> result =
                userController.getBankingPreference(digitalCustomerProfileId);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(responseEntity ->
                        responseEntity.getStatusCode().is2xxSuccessful() && responseEntity
                                .getBody().equals(response))
                .verifyComplete();
    }

    @Test
    void testGetMarketingPreference() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        MarketingNotificationPreferenceResponse response = new MarketingNotificationPreferenceResponse();
        when(userRegistrationService.getMarketingPreference(any(UUID.class)))
                .thenReturn(Mono.just(response));

        // Act
        Mono<ResponseEntity<MarketingNotificationPreferenceResponse>> result =
                userController.getMarketingPreference(digitalCustomerProfileId);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(responseEntity ->
                        responseEntity.getStatusCode().is2xxSuccessful()
                                && responseEntity.getBody().equals(response))
                .verifyComplete();
    }


    @Test
    void testGetUserNameByDeviceId() {
        Mono<UserNameResponse> mono = mock(Mono.class);
        when(mono.map(Mockito.<Function<UserNameResponse, Object>>any())).thenReturn(null);
        UserRegistrationServiceImpl userRegistrationService = mock(UserRegistrationServiceImpl.class);
        when(userRegistrationService.getUserNameInfoByCustomerDeviceId(Mockito.<Integer>any())).thenReturn(mono);
        Mono<ResponseEntity<UserNameResponse>> actualUserNameByDeviceId = (new UserController(userRegistrationService,
                mock(UserInfoService.class), mock(PinService.class), mock(DigitalCustomerShortcutsService.class),
                mock(DigitalCookiesPreferenceService.class), auditDigitalCustomerIdHolder,
                promotionOffers, digitalDeviceService,
                jsonUtils))
                .getUserNameByDeviceId(2);
        verify(userRegistrationService).getUserNameInfoByCustomerDeviceId(Mockito.<Integer>any());
        verify(mono).map(Mockito.<Function<UserNameResponse, Object>>any());
        assertNull(actualUserNameByDeviceId);
    }

    @Test
    void testGetUserNameByDeviceId2() {
        Mono<UserNameResponse> mono = mock(Mono.class);
        when(mono.map(Mockito.<Function<UserNameResponse, Object>>any())).thenReturn(null);
        UserRegistrationServiceImpl userRegistrationService = mock(UserRegistrationServiceImpl.class);
        when(userRegistrationService.getUserNameInfoByCustomerDeviceId(Mockito.<Integer>any())).thenReturn(mono);
        Mono<ResponseEntity<UserNameResponse>> actualUserNameByDeviceId = (new UserController(userRegistrationService,
                mock(UserInfoService.class), mock(PinService.class), mock(DigitalCustomerShortcutsService.class),
                mock(DigitalCookiesPreferenceService.class), auditDigitalCustomerIdHolder,
                promotionOffers, digitalDeviceService,
                jsonUtils))
                .getUserNameByDeviceId(2);
        verify(userRegistrationService).getUserNameInfoByCustomerDeviceId(Mockito.<Integer>any());
        verify(mono).map(Mockito.<Function<UserNameResponse, Object>>any());
        assertNull(actualUserNameByDeviceId);
    }

    @Test
    void testGetDigitalCustomerShortcut() {
        DigitalCustomerShortcutsService digitalCustomerShortcutsService = mock(DigitalCustomerShortcutsService.class);
        Mono<DigitalCustomerShortcutsResponse> justResult = Mono.just(new DigitalCustomerShortcutsResponse());
        when(digitalCustomerShortcutsService.getDigitalCustomerShortcut(Mockito.any())).thenReturn(justResult);
        UserInfoRepository customerRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        DigitalCookiePreferenceRepository digitalCookiePreferenceRepository = mock(
                DigitalCookiePreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        UserController userController = new UserController(
                new UserRegistrationServiceImpl(customerRepository, digitalCustomerProfileRepository,
                        bankingNotificationPreferenceRepository, marketingNotificationPreferencesRepository,
                        new DigitalCustomerProfileAndDeviceInjector(
                                mock(String.valueOf(userInfoServiceImpl())),
                                mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                                mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                                mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                                mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                                mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                        digitalCookiePreferenceRepository, mock(DigitalCustomerDeviceRepository.class),
                        mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                        mock(NotificationUtil.class)),
                mock(UserInfoService.class), pinService, digitalCustomerShortcutsService,
                digitalCookiesPreferenceService,
                auditDigitalCustomerIdHolder, promotionOffers, digitalDeviceService,
                jsonUtils);
        userController.getDigitalCustomerShortcut(UUID.randomUUID());
        verify(digitalCustomerShortcutsService).getDigitalCustomerShortcut(Mockito.any());
    }

    @Test
    void testGetDigitalCustomerShortcut2() {
        Mono<DigitalCustomerShortcutsResponse> mono = mock(Mono.class);
        Mono<Object> justResult = Mono.just("Data");
        when(mono.map(Mockito.<Function<DigitalCustomerShortcutsResponse, Object>>any())).thenReturn(justResult);
        DigitalCustomerShortcutsService digitalCustomerShortcutsService = mock(DigitalCustomerShortcutsService.class);
        when(digitalCustomerShortcutsService.getDigitalCustomerShortcut(Mockito.any())).thenReturn(mono);
        UserInfoRepository customerRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        DigitalCookiePreferenceRepository digitalCookiePreferenceRepository =
                mock(DigitalCookiePreferenceRepository.class);
        UserController userController = new UserController(
                new UserRegistrationServiceImpl(customerRepository, digitalCustomerProfileRepository,
                        bankingNotificationPreferenceRepository, marketingNotificationPreferencesRepository,
                        new DigitalCustomerProfileAndDeviceInjector(
                                mock(String.valueOf(userInfoServiceImpl())),
                                mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                                mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                                mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                                mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                                mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                        digitalCookiePreferenceRepository, mock(DigitalCustomerDeviceRepository.class),
                        mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                        mock(NotificationUtil.class)),
                mock(UserInfoService.class), pinService, digitalCustomerShortcutsService,
                digitalCookiesPreferenceService,
                auditDigitalCustomerIdHolder, promotionOffers, digitalDeviceService,
                jsonUtils);
        Mono<ResponseEntity<DigitalCustomerShortcutsResponse>> actualDigitalCustomerShortcut = userController
                .getDigitalCustomerShortcut(UUID.randomUUID());
        verify(digitalCustomerShortcutsService).getDigitalCustomerShortcut(Mockito.any());
        verify(mono).map(Mockito.<Function<DigitalCustomerShortcutsResponse, Object>>any());
        assertSame(justResult, actualDigitalCustomerShortcut);
    }


    @Test
    void testGetDigitalCookiePreference() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        DigitalCookiePreferenceResponse response = new DigitalCookiePreferenceResponse();
        when(userRegistrationService.getDigitalCookiePreference(any(UUID.class)))
                .thenReturn(Mono.just(response));

        // Act
        Mono<ResponseEntity<DigitalCookiePreferenceResponse>> result =
                userController.getDigitalCookiePreference(digitalCustomerProfileId);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(responseEntity ->
                        responseEntity.getStatusCode().is2xxSuccessful()
                                && responseEntity.getBody().equals(response))
                .verifyComplete();
    }

    @Test
    void validateNewPin() throws Exception {
        PinChangeResponse pinChangeResponse = PinChangeResponse.builder().statusCode(HttpStatus.OK.value()).build();
        PinChangeRequest pinChangeRequest = PinChangeRequest.builder()
                .digitalCustomerProfileId(UUID.randomUUID())
                .oldPin("123467")
                .newPin("123467")
                .build();
        UserInfoRepository customerRepository = mock(UserInfoRepository.class);
        DigitalCookiePreferenceRepository digitalCookiePreferenceRepository =
                mock(DigitalCookiePreferenceRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        UserController userController = new UserController(
                new UserRegistrationServiceImpl(customerRepository, digitalCustomerProfileRepository,
                        bankingNotificationPreferenceRepository, marketingNotificationPreferencesRepository,
                        new DigitalCustomerProfileAndDeviceInjector(
                                mock(String.valueOf(userInfoServiceImpl())),
                                mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                                mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                                mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                                mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                                mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                        digitalCookiePreferenceRepository, mock(DigitalCustomerDeviceRepository.class),
                        mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                        mock(NotificationUtil.class)),
                mock(UserInfoService.class), pinService, digitalCustomerShortcutsService,
                digitalCookiesPreferenceService,
                auditDigitalCustomerIdHolder, promotionOffers, digitalDeviceService,
                jsonUtils);
        when(pinService.validatePinHistory(Mockito.any())).thenReturn(pinChangeResponse);
        Assert.assertEquals(HttpStatus.OK.value(),
                userController.validatePinHistory(pinChangeRequest).getStatusCode().value());
    }

    @Test
    void saveOldPin() throws Exception {
        String digitalCustomerName = "Customer";
        PinChangeResponse pinChangeResponse = PinChangeResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("COMPLETED")
                .build();
        PinChangeRequest pinChangeRequest = PinChangeRequest.builder()
                .digitalCustomerProfileId(UUID.randomUUID())
                .oldPin("1234")
                .newPin("1234")
                .build();
        UserInfoRepository customerRepository = mock(UserInfoRepository.class);
        DigitalCookiePreferenceRepository digitalCookiePreferenceRepository = mock(
                DigitalCookiePreferenceRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        UserController userController = new UserController(
                new UserRegistrationServiceImpl(customerRepository, digitalCustomerProfileRepository,
                        bankingNotificationPreferenceRepository, marketingNotificationPreferencesRepository,
                        new DigitalCustomerProfileAndDeviceInjector(
                                mock(String.valueOf(userInfoServiceImpl())),
                                mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                                mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                                mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                                mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                                mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                        digitalCookiePreferenceRepository, mock(DigitalCustomerDeviceRepository.class),
                        mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                        mock(NotificationUtil.class)),
                mock(UserInfoService.class), pinService, digitalCustomerShortcutsService,
                digitalCookiesPreferenceService,
                auditDigitalCustomerIdHolder, promotionOffers, digitalDeviceService,
                jsonUtils);
        when(pinService.saveOldPin(pinChangeRequest)).thenReturn(pinChangeResponse);
        PinChangeResponse pinChangeResponse1 = userController.saveOldPin(pinChangeRequest, digitalCustomerName);
        assertEquals(pinChangeResponse.getStatusCode(), pinChangeResponse1.getStatusCode());
    }

    @Test
    void testSaveDigitalCookiePreference() {
        DigitalCookiesPreferenceService digitalCookiesPreferenceService = mock(
                DigitalCookiesPreferenceService.class);
        when(digitalCookiesPreferenceService.saveDigitalCookiesPreferences(Mockito.any(),
                Mockito.any()))
                .thenThrow(new DigitalCustomerProfileIdNotNullException("An error occurred"));
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(
                mock(UserInfoRepository.class), mock(DigitalCustomerProfileRepository.class),
                mock(BankingNotificationPreferenceRepository.class),
                mock(MarketingNotificationPreferencesRepository.class),
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));
        UserInfoService userInfoService = mock(UserInfoService.class);
        UserController userController = new UserController(userRegistrationService, userInfoService, pinService,
                new DigitalCustomerShortcutsServiceImpl(mock(DigitalCustomerShortcutsRepository.class),
                        mock(SesionHistoryRepository.class), mock(UserInfoRepository.class)),
                digitalCookiesPreferenceService, auditDigitalCustomerIdHolder, promotionOffers,
                digitalDeviceService,
                jsonUtils);
        UUID digitalCustomerProfileId = UUID.randomUUID();
        assertThrows(DigitalCookiePreferenceUpdateException.class, () -> userController
                .saveDigitalCookiePreference(digitalCustomerProfileId, "abc",
                        new DigitalCookiesPreferenceRequest()));
        verify(digitalCookiesPreferenceService).saveDigitalCookiesPreferences(Mockito.any(),
                Mockito.any());
    }

    @Test
    void testSaveDigitalCookiePreference2() {
        DigitalCookiesPreferenceService digitalCookiesPreferenceService = mock(
                DigitalCookiesPreferenceService.class);
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        when(digitalCookiesPreferenceService.saveDigitalCookiesPreferences(Mockito.any(),
                Mockito.any()))
                .thenThrow(new DigitalCustomerProfileIdNotFoundException(errorCode,
                        HttpStatus.NOT_FOUND,
                        FAILURE,
                        NOT_FOUND_ERROR_MESSAGE + UUID.randomUUID(),
                        new ArrayList<>()));
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(
                mock(UserInfoRepository.class), mock(DigitalCustomerProfileRepository.class),
                mock(BankingNotificationPreferenceRepository.class),
                mock(MarketingNotificationPreferencesRepository.class),
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));
        UserInfoService userInfoService = mock(UserInfoService.class);
        UserController userController = new UserController(userRegistrationService, userInfoService, pinService,
                new DigitalCustomerShortcutsServiceImpl(mock(DigitalCustomerShortcutsRepository.class),
                        mock(SesionHistoryRepository.class), mock(UserInfoRepository.class)),
                digitalCookiesPreferenceService, auditDigitalCustomerIdHolder, promotionOffers,
                digitalDeviceService,
                jsonUtils);
        UUID digitalCustomerProfileId = UUID.randomUUID();
        DigitalCookiesPreferenceRequest digitalCookiesPreferenceRequest = new DigitalCookiesPreferenceRequest();
        assertThrows(DigitalCustomerProfileIdNotFoundException.class,
                () -> userController.saveDigitalCookiePreference(
                        digitalCustomerProfileId, "avc", digitalCookiesPreferenceRequest));
        verify(digitalCookiesPreferenceService).saveDigitalCookiesPreferences(Mockito.any(),
                Mockito.any());
    }

    @Test
    void testSaveDigitalCookiePreference3() throws Exception {
        Mono<DigitalCookiesPreferenceResponse> mono = mock(Mono.class);
        DigitalCookiesPreferenceResponse.DigitalCookiesPreferenceResponseBuilder messageResult =
                DigitalCookiesPreferenceResponse
                        .builder()
                        .httpStatus(HttpStatus.CONTINUE)
                        .message("Not all who wander are lost");
        DigitalCookiesPreferenceResponse buildResult = messageResult.timestamp(
                        LocalDate.of(YEAR_1970, 1, 1).atStartOfDay())
                .build();
        when(mono.block()).thenReturn(buildResult);
        DigitalCookiesPreferenceService digitalCookiesPreferenceService = mock(DigitalCookiesPreferenceService.class);
        when(digitalCookiesPreferenceService.saveDigitalCookiesPreferences(Mockito.any(),
                Mockito.any())).thenReturn(mono);
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(
                mock(UserInfoRepository.class), mock(DigitalCustomerProfileRepository.class),
                mock(BankingNotificationPreferenceRepository.class),
                mock(MarketingNotificationPreferencesRepository.class),
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));
        UserInfoService userInfoService = mock(UserInfoService.class);
        UserController userController = new UserController(userRegistrationService, userInfoService, pinService,
                new DigitalCustomerShortcutsServiceImpl(mock(DigitalCustomerShortcutsRepository.class),
                        mock(SesionHistoryRepository.class), mock(UserInfoRepository.class)),
                digitalCookiesPreferenceService, auditDigitalCustomerIdHolder, promotionOffers,
                digitalDeviceService,
                jsonUtils);
        UUID digitalCustomerProfileId = UUID.randomUUID();
        userController.saveDigitalCookiePreference(digitalCustomerProfileId, "avp",
                new DigitalCookiesPreferenceRequest());
        verify(digitalCookiesPreferenceService).saveDigitalCookiesPreferences(Mockito.any(),
                Mockito.any());
        verify(mono).block();
    }

    @Test
    void testSaveDigitalCookiePreference4() throws Exception {
        Mono<DigitalCookiesPreferenceResponse> mono = mock(Mono.class);
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        when(mono.block()).thenThrow(new DigitalCustomerProfileIdNotFoundException(errorCode,
                HttpStatus.NOT_FOUND,
                FAILURE,
                NOT_FOUND_ERROR_MESSAGE + UUID.randomUUID(),
                new ArrayList<>()));
        DigitalCookiesPreferenceService digitalCookiesPreferenceService = mock(DigitalCookiesPreferenceService.class);
        when(digitalCookiesPreferenceService.saveDigitalCookiesPreferences(Mockito.any(),
                Mockito.any())).thenReturn(mono);
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(
                mock(UserInfoRepository.class), mock(DigitalCustomerProfileRepository.class),
                mock(BankingNotificationPreferenceRepository.class),
                mock(MarketingNotificationPreferencesRepository.class),
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));
        UserInfoService userInfoService = mock(UserInfoService.class);
        UserController userController = new UserController(userRegistrationService, userInfoService, pinService,
                new DigitalCustomerShortcutsServiceImpl(mock(DigitalCustomerShortcutsRepository.class),
                        mock(SesionHistoryRepository.class), mock(UserInfoRepository.class)),
                digitalCookiesPreferenceService, auditDigitalCustomerIdHolder, promotionOffers,
                digitalDeviceService,
                jsonUtils);
        UUID digitalCustomerProfileId = UUID.randomUUID();
        DigitalCookiesPreferenceRequest digitalCookiesPreferenceRequest = new DigitalCookiesPreferenceRequest();
        assertThrows(DigitalCustomerProfileIdNotFoundException.class,
                () -> userController.saveDigitalCookiePreference(
                        digitalCustomerProfileId, "Vij", digitalCookiesPreferenceRequest));
        verify(digitalCookiesPreferenceService).saveDigitalCookiesPreferences(Mockito.any(),
                Mockito.any());
        verify(mono).block();
    }

    @Test
    void testGetCustomerSessionHistory() {
        CustomerSessionHistoryResponse customerSessionHistoryResponse = new CustomerSessionHistoryResponse();
        List<CustomerSessionHistory> customerSessionHistoryList = new ArrayList<>();
        CustomerSessionHistory customerSessionHistory = new CustomerSessionHistory();
        customerSessionHistory.setActivityName("login");
        customerSessionHistory.setActivityDate(new Date().toString());
        customerSessionHistory.setActivityChannel("Web");
        customerSessionHistory.setActivityTime("10:11:12");
        customerSessionHistoryList.add(customerSessionHistory);
        customerSessionHistoryResponse.setCustomerSessionHistory(
                customerSessionHistoryList);
        when(digitalCustomerShortcutsService.getCustomerSessionHistoryResponse(
                Mockito.any(), Mockito.<Integer>any(),
                Mockito.<Integer>any(), Mockito.any())).thenReturn(customerSessionHistoryResponse);
        UserController userController = new UserController(
                new UserRegistrationServiceImpl(mock(UserInfoRepository.class),
                        mock(DigitalCustomerProfileRepository.class),
                        mock(BankingNotificationPreferenceRepository.class),
                        mock(MarketingNotificationPreferencesRepository.class),
                        new DigitalCustomerProfileAndDeviceInjector(
                                mock(String.valueOf(userInfoServiceImpl())),
                                mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                                mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                                mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                                mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                                mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                        mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                        mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                        mock(NotificationUtil.class)),
                mock(UserInfoService.class), pinService, digitalCustomerShortcutsService,
                digitalCookiesPreferenceService, auditDigitalCustomerIdHolder, promotionOffers,
                digitalDeviceService,
                jsonUtils);
        SessionHistoryFilterRequest request = getSessionHistoryFilterRequest();
        ResponseEntity<CustomerSessionHistoryResponse> customerSessionHistoryResponseEntity = userController
                .getCustomerSessionHistory(UUID.randomUUID(), 1, numInt, request);
        assertNotNull(customerSessionHistoryResponseEntity);
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
    void testGetCustomerSessionHistoryEmptyResponse() {
        CustomerSessionHistoryResponse customerSessionHistoryResponse = new CustomerSessionHistoryResponse();
        List<CustomerSessionHistory> customerSessionHistoryList = new ArrayList<>();
        customerSessionHistoryResponse.setCustomerSessionHistory(
                customerSessionHistoryList);
        when(digitalCustomerShortcutsService.getCustomerSessionHistoryResponse(
                Mockito.any(), Mockito.<Integer>any(),
                Mockito.<Integer>any(), Mockito.any())).thenReturn(customerSessionHistoryResponse);
        UserController userController = new UserController(
                new UserRegistrationServiceImpl(mock(UserInfoRepository.class),
                        mock(DigitalCustomerProfileRepository.class),
                        mock(BankingNotificationPreferenceRepository.class),
                        mock(MarketingNotificationPreferencesRepository.class),
                        new DigitalCustomerProfileAndDeviceInjector(
                                mock(String.valueOf(userInfoServiceImpl())),
                                mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                                mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                                mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                                mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                                mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                        mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                        mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                        mock(NotificationUtil.class)),
                mock(UserInfoService.class), pinService, digitalCustomerShortcutsService,
                digitalCookiesPreferenceService, auditDigitalCustomerIdHolder, promotionOffers,
                digitalDeviceService,
                jsonUtils);
        SessionHistoryFilterRequest request = getSessionHistoryFilterRequest();
        ResponseEntity<CustomerSessionHistoryResponse> customerSessionHistoryResponseEntity = userController
                .getCustomerSessionHistory(UUID.randomUUID(), 1, numInt, request);
        assertNotNull(customerSessionHistoryResponseEntity);
    }

    @Test
    void testUpdateBankingNotificationPreference()   {
        when(userInfoRepository.findUserNameByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn("janedoe");
        DigitalCustomerProfileRepository digitalCustomerProfileRepository =
                mock(DigitalCustomerProfileRepository.class);
        Optional<DigitalCustomerProfile> ofResult = Optional.of(mock(DigitalCustomerProfile.class));
        when(digitalCustomerProfileRepository.findById(Mockito.<UUID>any())).thenReturn(ofResult);

        DigitalNotificationPreference digitalNotificationPreference = new DigitalNotificationPreference();
        digitalNotificationPreference.setDigitalBankingNotificationPreferenceId(1);
        digitalNotificationPreference.setDigitalCustomerProfileId(UUID.randomUUID());
        digitalNotificationPreference.setEmailNotificationBanking(true);
        digitalNotificationPreference.setMobilePushNotificationBanking(true);
        digitalNotificationPreference.setNotificationCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalNotificationPreference.setNotificationCreationDate(
                LocalDate.of(YEAR_1970, 1, 1).atStartOfDay());
        digitalNotificationPreference.setNotificationModificationDate(
                LocalDate.of(YEAR_1970, 1, 1).atStartOfDay());
        digitalNotificationPreference.setNotificationModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalNotificationPreference.setSmsNotificationBanking(true);

        DigitalNotificationPreference digitalNotificationPreference2 = new DigitalNotificationPreference();
        digitalNotificationPreference2.setDigitalBankingNotificationPreferenceId(1);
        digitalNotificationPreference2.setDigitalCustomerProfileId(UUID.randomUUID());
        digitalNotificationPreference2.setEmailNotificationBanking(true);
        digitalNotificationPreference2.setMobilePushNotificationBanking(true);
        digitalNotificationPreference2.setNotificationCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalNotificationPreference2.setNotificationCreationDate(
                LocalDate.of(YEAR_1970, 1, 1).atStartOfDay());
        digitalNotificationPreference2.setNotificationModificationDate(
                LocalDate.of(YEAR_1970, 1, 1).atStartOfDay());
        digitalNotificationPreference2.setNotificationModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalNotificationPreference2.setSmsNotificationBanking(true);
        Optional<DigitalNotificationPreference> ofResult2 = Optional.of(digitalNotificationPreference2);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        when(bankingNotificationPreferenceRepository.saveAndFlush(Mockito.<DigitalNotificationPreference>any()))
                .thenReturn(digitalNotificationPreference);
        when(bankingNotificationPreferenceRepository.findByDigitalCustomerProfileId(Mockito.<UUID>any()))
                .thenReturn(ofResult2);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        DigitalCustomerProfileAndDeviceInjector digitalCustomerProfileAndDeviceInjector =
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(DigitalCustomerProfileRepository.class), mock(DigitalCustomerDeviceRepository.class),
                        mock(DigitalDeviceLinkRepository.class), mock(DigitalCookiePreferenceRepository.class),
                        mock(DigitalCustomerDeviceAuditRepository.class));

        DigitalCookiePreferenceRepository digitalCookiePreferenceRepository =
                mock(DigitalCookiePreferenceRepository.class);
        DigitalCustomerDeviceRepository digitalCustomerDeviceRepository = mock(DigitalCustomerDeviceRepository.class);
        DigitalDeviceLinkRepository digitalDeviceLinkRepository = mock(DigitalDeviceLinkRepository.class);
        ConfigurationServiceClient configurationServiceClient = mock(ConfigurationServiceClient.class);
        UserRegistrationServiceImpl userRegistrationServiceLocal = new UserRegistrationServiceImpl(userInfoRepository,
                digitalCustomerProfileRepository, bankingNotificationPreferenceRepository,
                marketingNotificationPreferencesRepository, digitalCustomerProfileAndDeviceInjector,
                digitalCookiePreferenceRepository, digitalCustomerDeviceRepository, digitalDeviceLinkRepository,
                configurationServiceClient,
                mock(NotificationUtil.class));

        DigitalCustomerDeviceRepository digitalCustomerDeviceRepository2 = mock(DigitalCustomerDeviceRepository.class);
        DigitalDeviceLinkRepository digitalDeviceLinkRepository2 = mock(DigitalDeviceLinkRepository.class);
        LoginAttemptRepository loginAttemptRepository = mock(LoginAttemptRepository.class);
        DigitalCustomerPwdRepository digitalCustomerPwdRepository = mock(DigitalCustomerPwdRepository.class);
        AppConfig appConfig = new AppConfig();
        UserInfoRepository userInfoRepository2 = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository2 =
                mock(DigitalCustomerProfileRepository.class);
        UserInfoServiceImpl userInfoServiceLocal = new UserInfoServiceImpl(digitalCustomerDeviceRepository2,
                digitalDeviceLinkRepository2, loginAttemptRepository, digitalCustomerPwdRepository, appConfig,
                userInfoRepository2, digitalCustomerProfileRepository2,
                new DigitalCustomerAlertServiceImpl(mock(DigitalCustomerAlertRepository.class),
                        mock(DigitalDocdbAlertRefRepository.class), mock(DigitalCustomerProfileRepository.class)),
                mock(String.valueOf(NotificationOrchestratorServiceClient.class)),
                mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                mock(String.valueOf(ConfigurationServiceClient.class)), pinRepository,
                mock(String.valueOf(NotificationUtil.class)));

        PinServiceImpl pinServiceLocal = new PinServiceImpl(mock(PinRepository.class));
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsServiceLocal =
                new DigitalCustomerShortcutsServiceImpl(
                        mock(DigitalCustomerShortcutsRepository.class), mock(SesionHistoryRepository.class),
                        mock(UserInfoRepository.class));

        DigitalCookiesPreferenceService digitalCookiesPreferenceServiceLocal =
                mock(DigitalCookiesPreferenceService.class);
        AuditDigitalCustomerHolder auditDigitalCustomerHolder = new AuditDigitalCustomerHolder();
        PromotionOffers promotionOffersLocal = mock(PromotionOffers.class);
        UserController userControllerLocal = new UserController(userRegistrationServiceLocal, userInfoServiceLocal,
                pinServiceLocal,
                digitalCustomerShortcutsServiceLocal, digitalCookiesPreferenceServiceLocal,
                auditDigitalCustomerHolder, promotionOffersLocal,
                new DigitalCustomerDeviceServiceImpl(mock(DigitalCustomerDeviceRepository.class)), jsonUtils);
        UUID digitalCustomerProfileId = UUID.randomUUID();
        BankingNotificationPreferenceRequest request =
                new BankingNotificationPreferenceRequest();
        request.setNotificationPreferenceList(Map.of("hdrEmail", true));
        userControllerLocal.updateBankingNotificationPreference(digitalCustomerProfileId, request);
        verify(bankingNotificationPreferenceRepository).findByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(digitalCustomerProfileRepository).findById(Mockito.<UUID>any());
        verify(userInfoRepository).findUserNameByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(bankingNotificationPreferenceRepository).saveAndFlush(Mockito.<DigitalNotificationPreference>any());
    }

    @Test
    void testUpdateMarketingNotificationPreference() throws JsonProcessingException {
        DigitalMarketingNotificationPreference marketingNotificationPreference = new
                DigitalMarketingNotificationPreference();
        marketingNotificationPreference.setNotificationCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        marketingNotificationPreference.setNotificationCreationDate(LocalDateTime.of(
                YEAR_1970, 1, 1, 0, 0));
        marketingNotificationPreference.setDigitalCustomerProfileId(UUID.randomUUID());
        marketingNotificationPreference
                .setDigitalMarketingNotificationPreferenceId(DIGITAL_BANKING_NOTIFICATION_PREFERENCE_ID);
        marketingNotificationPreference.setMarketingEmailNotification(true);
        marketingNotificationPreference.setMarketingOnlineNotification(true);
        marketingNotificationPreference.setMarketingPostNotification(true);
        marketingNotificationPreference.setMarketingSmsNotification(true);
        marketingNotificationPreference.setMarketingTelephoneNotification(true);
        marketingNotificationPreference.setNotificationModifiedBy("2020-03-01");
        marketingNotificationPreference.setNotificationModificationDate(LocalDateTime.of(YEAR_1970,
                1, 1, 0, 0));
        Optional<DigitalMarketingNotificationPreference> ofResult = Optional.of(marketingNotificationPreference);
        DigitalMarketingNotificationPreference marketingNotificationPreference2 = new
                DigitalMarketingNotificationPreference();
        marketingNotificationPreference2.setNotificationCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        marketingNotificationPreference2.setNotificationCreationDate(LocalDateTime.of(
                YEAR_1970, 1, 1, 0, 0));
        marketingNotificationPreference2.setDigitalCustomerProfileId(UUID.randomUUID());
        marketingNotificationPreference2
                .setDigitalMarketingNotificationPreferenceId(DIGITAL_BANKING_NOTIFICATION_PREFERENCE_ID);
        marketingNotificationPreference2.setMarketingEmailNotification(true);
        marketingNotificationPreference2.setMarketingOnlineNotification(true);
        marketingNotificationPreference2.setMarketingPostNotification(true);
        marketingNotificationPreference2.setMarketingSmsNotification(true);
        marketingNotificationPreference2.setMarketingTelephoneNotification(true);
        marketingNotificationPreference2.setNotificationModifiedBy("2020-03-01");
        marketingNotificationPreference2.setNotificationModificationDate(
                LocalDateTime.of(YEAR_1970, 1, 1, 0, 0));
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        when(marketingNotificationPreferencesRepository.saveAndFlush(
                Mockito.any()))
                .thenReturn(marketingNotificationPreference2);
        when(marketingNotificationPreferencesRepository.findByDigitalCustomerProfileId(Mockito.any()))
                .thenReturn(ofResult);
        UserInfoRepository customerRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        DigitalCustomerProfile digitalCustomerProfile = new DigitalCustomerProfile();
        Optional<DigitalCustomerProfile> result = Optional.of(digitalCustomerProfile);
        when(digitalCustomerProfileRepository.findById(Mockito.any()))
                .thenReturn(result);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(customerRepository,
                digitalCustomerProfileRepository, bankingNotificationPreferenceRepository,
                marketingNotificationPreferencesRepository,
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));
        UserInfoService userInfoService = mock(UserInfoService.class);
        UserController userController = new UserController(userRegistrationService, userInfoService, pinService,
                new DigitalCustomerShortcutsServiceImpl(mock(DigitalCustomerShortcutsRepository.class),
                        mock(SesionHistoryRepository.class), mock(UserInfoRepository.class)),
                mock(DigitalCookiesPreferenceService.class), auditDigitalCustomerIdHolder,
                promotionOffers, digitalDeviceService,
                jsonUtils);
        UUID digitalCustomerProfileId = UUID.randomUUID();
        MarketingNotificationPreferenceRequest request = new MarketingNotificationPreferenceRequest(new HashMap<>());
        userController.updateMarketingNotificationPreference(digitalCustomerProfileId, request);
        verify(marketingNotificationPreferencesRepository).findByDigitalCustomerProfileId(Mockito.any());
        verify(marketingNotificationPreferencesRepository)
                .saveAndFlush(Mockito.any());
    }

    @Test
    void testCheckPinExistsBasedOnDigitalDeviceUdid() {
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(userInfoRepository,
                digitalCustomerProfileRepository, bankingNotificationPreferenceRepository,
                marketingNotificationPreferencesRepository,
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));

        DigitalCustomerDeviceRepository digitalCustomerDeviceRepository = mock(DigitalCustomerDeviceRepository.class);
        DigitalDeviceLinkRepository digitalDeviceLinkRepository = mock(DigitalDeviceLinkRepository.class);
        LoginAttemptRepository loginAttemptRepository = mock(LoginAttemptRepository.class);
        UserInfoServiceImpl userInfoService = new UserInfoServiceImpl(digitalCustomerDeviceRepository,
                digitalDeviceLinkRepository, loginAttemptRepository,
                mock(DigitalCustomerPwdRepository.class), new AppConfig(),
                mock(UserInfoRepository.class), mock(DigitalCustomerProfileRepository.class),
                mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                mock(String.valueOf(DigitalCustomerAlertService.class)),
                mock(String.valueOf(NotificationOrchestratorServiceClient.class)),
                mock(ConfigurationServiceClient.class), pinRepository,
                mock(NotificationUtil.class));
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsService =
                new DigitalCustomerShortcutsServiceImpl(mock(DigitalCustomerShortcutsRepository.class),
                        mock(SesionHistoryRepository.class), mock(UserInfoRepository.class));
        DigitalCookiesPreferenceService digitalCookiesPreferenceService =
                mock(DigitalCookiesPreferenceService.class);
        UserController userController1 = new UserController(userRegistrationService, userInfoService, pinService,
                digitalCustomerShortcutsService, digitalCookiesPreferenceService, new AuditDigitalCustomerHolder(),
                promotionOffers, digitalDeviceService,
                jsonUtils);
        assertThrows(IllegalArgumentException.class,
                () -> (userController1).checkPinExistsBasedOnDigitalDeviceUdid(null));
    }

    @Test
    void testCheckPinExistsBasedOnDigitalDeviceUdid2() {
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);
        Optional<Boolean> ofResult = Optional.of(true);
        when(userInfoRepository.checkPinExistsBasedOnDigitalDeviceUdid(Mockito.any())).thenReturn(ofResult);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(userInfoRepository,
                digitalCustomerProfileRepository, bankingNotificationPreferenceRepository,
                marketingNotificationPreferencesRepository,
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));
        UserInfoServiceImpl userInfoService = userInfoServiceImpl();
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsService =
                new DigitalCustomerShortcutsServiceImpl(mock(DigitalCustomerShortcutsRepository.class),
                        mock(SesionHistoryRepository.class), mock(UserInfoRepository.class));
        DigitalCookiesPreferenceService digitalCookiesPreferenceService = mock(DigitalCookiesPreferenceService.class);
        (new UserController(userRegistrationService, userInfoService, pinService, digitalCustomerShortcutsService,
                digitalCookiesPreferenceService, new AuditDigitalCustomerHolder(),
                promotionOffers, digitalDeviceService,
                jsonUtils))
                .checkPinExistsBasedOnDigitalDeviceUdid("Digital Device Udid");
        verify(userInfoRepository).checkPinExistsBasedOnDigitalDeviceUdid(Mockito.any());
    }

    @Test
    void testCheckPinExistsBasedOnDigitalDeviceUdid3() {
        UserRegistrationService userRegistrationService = mock(UserRegistrationService.class);
        UserInfoServiceImpl userInfoService = userInfoServiceImpl();
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsService =
                new DigitalCustomerShortcutsServiceImpl(mock(DigitalCustomerShortcutsRepository.class),
                        mock(SesionHistoryRepository.class), mock(UserInfoRepository.class));
        DigitalCookiesPreferenceService digitalCookiesPreferenceService = mock(DigitalCookiesPreferenceService.class);
        UserController userController1 = new UserController(userRegistrationService, userInfoService, pinService,
                digitalCustomerShortcutsService, digitalCookiesPreferenceService, new AuditDigitalCustomerHolder(),
                promotionOffers, digitalDeviceService,
                jsonUtils);
        assertThrows(IllegalArgumentException.class, () -> (userController1)
                .checkPinExistsBasedOnDigitalDeviceUdid(""));
    }

    @Test
    void testCheckPinExistsBasedOnDigitalDeviceUdid4() {
        Mono<CheckPinStatusResponse> mono = mock(Mono.class);
        when(mono.map(Mockito.<Function<CheckPinStatusResponse, Object>>any())).thenReturn(null);
        UserRegistrationService userRegistrationService = mock(UserRegistrationService.class);
        when(userRegistrationService.checkPinExistsBasedOnDigitalDeviceId(Mockito.any())).thenReturn(mono);
        UserInfoServiceImpl userInfoService = userInfoServiceImpl();
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsService =
                new DigitalCustomerShortcutsServiceImpl(mock(DigitalCustomerShortcutsRepository.class),
                        mock(SesionHistoryRepository.class), mock(UserInfoRepository.class));
        DigitalCookiesPreferenceService digitalCookiesPreferenceService =
                mock(DigitalCookiesPreferenceService.class);
        Mono<ResponseEntity<CheckPinStatusResponse>> actualCheckPinExistsBasedOnDigitalDeviceUdidResult =
                (new UserController(
                        userRegistrationService, userInfoService, pinService, digitalCustomerShortcutsService,
                        digitalCookiesPreferenceService, new AuditDigitalCustomerHolder(),
                        promotionOffers, digitalDeviceService,
                        jsonUtils))
                        .checkPinExistsBasedOnDigitalDeviceUdid("Digital Device Udid");
        verify(userRegistrationService).checkPinExistsBasedOnDigitalDeviceId(Mockito.any());
        verify(mono).map(Mockito.<Function<CheckPinStatusResponse, Object>>any());
        assertNull(actualCheckPinExistsBasedOnDigitalDeviceUdidResult);
    }

    @Test
    void testFetchDeviceInfo() {
        UserInfoService userInfoService = mock(UserInfoService.class);
        DeviceInfoResponse buildResult = DeviceInfoResponse.builder()
                .deviceToken("ABC123")
                .deviceType("Device Type")
                .digitalCustomerDeviceId(1)
                .build();
        when(userInfoService.getUserDeviceInfo(Mockito.any())).thenReturn(buildResult);
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(userInfoRepository,
                digitalCustomerProfileRepository, bankingNotificationPreferenceRepository,
                marketingNotificationPreferencesRepository,
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));
        UserController userController = new UserController(userRegistrationService, userInfoService, pinService,
                new DigitalCustomerShortcutsServiceImpl(mock(DigitalCustomerShortcutsRepository.class),
                        mock(SesionHistoryRepository.class), mock(UserInfoRepository.class)),
                mock(DigitalCookiesPreferenceService.class), auditDigitalCustomerIdHolder,
                promotionOffers, digitalDeviceService,
                jsonUtils);
        userController.fetchDeviceInfo(UUID.randomUUID());
        verify(userInfoService).getUserDeviceInfo(Mockito.any());
    }

    @Test
    void testUpdateLoginAttemptDetailsByUsername() {
        UserInfoServiceImpl userInfoService = mock(UserInfoServiceImpl.class);
        doNothing().when(userInfoService).updateFailureAttemptDetailsByUsername(Mockito.any());
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(userInfoRepository,
                digitalCustomerProfileRepository, bankingNotificationPreferenceRepository,
                marketingNotificationPreferencesRepository,
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));
        (new UserController(userRegistrationService, userInfoService, pinService,
                new DigitalCustomerShortcutsServiceImpl(mock(DigitalCustomerShortcutsRepository.class),
                        mock(SesionHistoryRepository.class), mock(UserInfoRepository.class)),
                mock(DigitalCookiesPreferenceService.class), auditDigitalCustomerIdHolder,
                promotionOffers, digitalDeviceService,
                jsonUtils))
                .updateLoginAttemptDetailsByUsername(null);
        verify(userInfoService).updateFailureAttemptDetailsByUsername(Mockito.any());
    }

    @Test
    void testGetStatusSuccess() {
        String digitalUserName = "AVP@gmail.com";
        Mono<UserStatusResponse> expectedResponse = Mono.just(new UserStatusResponse(
                "ACTIVE", false, false));
        when(userInfoService.getUserStatus(digitalUserName)).thenReturn(expectedResponse);
        Mono<ResponseEntity<UserStatusResponse>> responseEntity =
                userController.getStatus(digitalUserName);
        assertNotNull(responseEntity);
        assertEquals(expectedResponse.block().getAccountStatus(), responseEntity.block().getBody().getAccountStatus());
        verify(userInfoService).getUserStatus(Mockito.any());
    }

    @Test
    void testGetStatusNotFound() {
        String digitalUserName = "AP@gmail.com";
        when(userInfoService.getUserStatus(digitalUserName))
                .thenThrow(DigitalCustomerProfileIdNotFoundException.class);
        assertThrows(DigitalCustomerProfileIdNotFoundException.class, () ->
                userController.getStatus(digitalUserName));
        verify(userInfoService).getUserStatus(digitalUserName);
    }

    @Test
    void testUpdateTermsConditionsAndCookies() {
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(userInfoRepository,
                digitalCustomerProfileRepository, bankingNotificationPreferenceRepository,
                marketingNotificationPreferencesRepository,
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));
        UserInfoServiceImpl userInfoService = userInfoServiceImpl();
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsService =
                new DigitalCustomerShortcutsServiceImpl(mock(DigitalCustomerShortcutsRepository.class),
                        mock(SesionHistoryRepository.class), mock(UserInfoRepository.class));
        DigitalCookiesPreferenceService digitalCookiesPreferenceService = mock(DigitalCookiesPreferenceService.class);
        UserController userController = new UserController(userRegistrationService, userInfoService, pinService,
                digitalCustomerShortcutsService, digitalCookiesPreferenceService, new AuditDigitalCustomerHolder(),
                promotionOffers, digitalDeviceService,
                jsonUtils);
        TermsConditionsAndCookiesRequest request = new TermsConditionsAndCookiesRequest();
        assertThrows(InvalidRequestException.class,
                () -> userController.updateTermsConditionsAndCookies("42",
                        "2020-03-01", request));
    }

    @Test
    void testUpdateTermsConditionsAndCookies1() {
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(userInfoRepository,
                digitalCustomerProfileRepository, bankingNotificationPreferenceRepository,
                marketingNotificationPreferencesRepository,
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));
        UserInfoServiceImpl userInfoService = userInfoServiceImpl();
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsService =
                new DigitalCustomerShortcutsServiceImpl(mock(DigitalCustomerShortcutsRepository.class),
                        mock(SesionHistoryRepository.class), mock(UserInfoRepository.class));
        DigitalCookiesPreferenceService digitalCookiesPreferenceService = mock(DigitalCookiesPreferenceService.class);
        UserController userController = new UserController(userRegistrationService, userInfoService, pinService,
                digitalCustomerShortcutsService, digitalCookiesPreferenceService, new AuditDigitalCustomerHolder(),
                promotionOffers, digitalDeviceService,
                jsonUtils);
        TermsConditionsAndCookiesRequest request = new TermsConditionsAndCookiesRequest();
        assertThrows(DeviceIdParamNotFoundException.class,
                () -> userController.updateTermsConditionsAndCookies(null, "terms", request));
    }

    @Test
    void testGetTermsConditionAndCookiesInfoByDeviceId() {
        DigitalCustomerDevice digitalCustomerDevice = mock(DigitalCustomerDevice.class);
        when(digitalCustomerDevice.getFunctionalCookie()).thenReturn(true);
        when(digitalCustomerDevice.getPerformanceCookie()).thenReturn(true);
        when(digitalCustomerDevice.getStrictlyAcceptanceCookie()).thenReturn(true);
        when(digitalCustomerDevice.getTermsAndConditions()).thenReturn(true);
        Optional<DigitalCustomerDevice> ofResult = Optional.of(digitalCustomerDevice);
        DigitalCustomerDeviceRepository digitalCustomerDeviceRepository = mock(DigitalCustomerDeviceRepository.class);
        when(digitalCustomerDeviceRepository.findByDigitalDeviceUdid(Mockito.any())).thenReturn(ofResult);
        DigitalDeviceLinkRepository digitalDeviceLinkRepository = mock(DigitalDeviceLinkRepository.class);
        LoginAttemptRepository loginAttemptRepository = mock(LoginAttemptRepository.class);
        UserInfoServiceImpl userInfoService = new UserInfoServiceImpl(digitalCustomerDeviceRepository,
                digitalDeviceLinkRepository, loginAttemptRepository,
                mock(DigitalCustomerPwdRepository.class), new AppConfig(),
                mock(UserInfoRepository.class), mock(DigitalCustomerProfileRepository.class),
                mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                mock(String.valueOf(DigitalCustomerAlertService.class)),
                mock(String.valueOf(NotificationOrchestratorServiceClient.class)),
                mock(ConfigurationServiceClient.class), pinRepository,
                mock(NotificationUtil.class));
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(userInfoRepository,
                digitalCustomerProfileRepository, bankingNotificationPreferenceRepository,
                marketingNotificationPreferencesRepository,
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsService =
                new DigitalCustomerShortcutsServiceImpl(mock(DigitalCustomerShortcutsRepository.class),
                        mock(SesionHistoryRepository.class), mock(UserInfoRepository.class));
        DigitalCookiesPreferenceService digitalCookiesPreferenceService =
                mock(DigitalCookiesPreferenceService.class);
        ResponseEntity<Mono<GetTermsConditionAndCookiesInfoResponse>> actualTermsConditionAndCookiesInfoByDeviceId
                = (new UserController(
                userRegistrationService, userInfoService, pinService, digitalCustomerShortcutsService,
                digitalCookiesPreferenceService, new AuditDigitalCustomerHolder(),
                promotionOffers, digitalDeviceService,
                jsonUtils))
                .getTermsConditionAndCookiesInfoByDeviceId("42");
        verify(digitalCustomerDevice).getFunctionalCookie();
        verify(digitalCustomerDevice).getPerformanceCookie();
        verify(digitalCustomerDevice).getStrictlyAcceptanceCookie();
        verify(digitalCustomerDevice).getTermsAndConditions();
        verify(digitalCustomerDeviceRepository).findByDigitalDeviceUdid(Mockito.any());
        assertEquals(okStatus, actualTermsConditionAndCookiesInfoByDeviceId.getStatusCodeValue());
        assertTrue(actualTermsConditionAndCookiesInfoByDeviceId.hasBody());
        assertTrue(actualTermsConditionAndCookiesInfoByDeviceId.getHeaders().isEmpty());
    }

    @Test
    void testCheckMfaStatusBasedOnDigitalDeviceUdid() {
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(userInfoRepository,
                digitalCustomerProfileRepository, bankingNotificationPreferenceRepository,
                marketingNotificationPreferencesRepository,
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));
        UserInfoServiceImpl userInfoService = userInfoServiceImpl();
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsService = new
                DigitalCustomerShortcutsServiceImpl(
                mock(DigitalCustomerShortcutsRepository.class), mock(SesionHistoryRepository.class),
                mock(UserInfoRepository.class));
        UserController controller = new UserController(
                userRegistrationService, userInfoService, pinService, digitalCustomerShortcutsService,
                digitalCookiesPreferenceService, new AuditDigitalCustomerHolder(), mock(PromotionOffers.class),
                digitalDeviceService,
                jsonUtils);
        assertThrows(IllegalArgumentException.class,
                () -> (controller)
                        .checkMfaStatusBasedOnDigitalDeviceUdid(null));
    }

    @Test
    void testCheckMfaStatusBasedOnDigitalDeviceUdid2() {
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);
        Optional<Boolean> ofResult = Optional.of(true);
        when(userInfoRepository.checkMfaStatusBasedOnDigitalDeviceUdid(Mockito.any())).thenReturn(ofResult);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(userInfoRepository,
                digitalCustomerProfileRepository, bankingNotificationPreferenceRepository,
                marketingNotificationPreferencesRepository,
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));
        UserInfoServiceImpl userInfoService = userInfoServiceImpl();
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsService = new DigitalCustomerShortcutsServiceImpl(
                mock(DigitalCustomerShortcutsRepository.class), mock(SesionHistoryRepository.class),
                mock(UserInfoRepository.class));
        DigitalCookiesPreferenceService digitalCookiesPreferenceService = mock(DigitalCookiesPreferenceService.class);
        (new UserController(userRegistrationService, userInfoService, pinService, digitalCustomerShortcutsService,
                digitalCookiesPreferenceService, new AuditDigitalCustomerHolder(),
                mock(PromotionOffers.class), digitalDeviceService,
                jsonUtils))
                .checkMfaStatusBasedOnDigitalDeviceUdid("Digital Device Udid");
        verify(userInfoRepository).checkMfaStatusBasedOnDigitalDeviceUdid(Mockito.any());
    }

    @Test
    void testCheckMfaStatusBasedOnDigitalDeviceUdid3() {
        UserRegistrationService userRegistrationService = mock(UserRegistrationService.class);
        UserInfoServiceImpl userInfoService = userInfoServiceImpl();
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsService = new DigitalCustomerShortcutsServiceImpl(
                mock(DigitalCustomerShortcutsRepository.class), mock(SesionHistoryRepository.class),
                mock(UserInfoRepository.class));
        UserController controller = new
                UserController(userRegistrationService, userInfoService, pinService, digitalCustomerShortcutsService,
                digitalCookiesPreferenceService, new AuditDigitalCustomerHolder(),
                mock(PromotionOffers.class), digitalDeviceService,
                jsonUtils);
        assertThrows(IllegalArgumentException.class,
                () -> (controller)
                        .checkMfaStatusBasedOnDigitalDeviceUdid(""));
    }

    @Test
    void testSavePublicKeySuccess() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserPublicKeyRequest userPublicKeyRequest = new UserPublicKeyRequest();
        userPublicKeyRequest.setDevicePublicKey("3F2594E0-4F89-41D3-9A0C-0305E62D3340");
        userPublicKeyRequest.setDevicePublicKey("MIGfMA0GCSqGSIb3");
        Mockito.when(userRegistrationService.saveUserPublicKeyForBioMetric(eq(digitalCustomerProfileId), any()))
                .thenReturn(UserAPIBaseResponse.builder()
                        .httpStatus(HttpStatus.OK)
                        .status("Success")
                        .message("Public key saved successfully")
                        .build());
        ResponseEntity<UserAPIBaseResponse> responseEntity = userController
                .savePublicKeyBioMetric(digitalCustomerProfileId, userPublicKeyRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Success", responseEntity.getBody().getStatus());
        assertEquals("Public key saved successfully", responseEntity.getBody().getMessage());
    }

    @Test
    void getUserInfoReturnsUserInfoResponse() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        String digitalDeviceUdid = "835185ffd672ab9c";
        UserInfoResponse expectedResponse = new UserInfoResponse();
        UserDetailDto userDetailsDTO = new UserDetailDto();
        when(userRegistrationService.getUserInfo(digitalCustomerProfileId, digitalDeviceUdid))
                .thenReturn(Mono.just(expectedResponse));
        when(userRegistrationService.buildUserDetailsDTO(expectedResponse)).thenReturn(userDetailsDTO);
        Mono<ResponseEntity<UserInfoResponse>> result = userController
                .getUserInfo(digitalCustomerProfileId, digitalDeviceUdid);
        StepVerifier.create(result).assertNext(responseEntity -> {
            assert responseEntity.getStatusCode() == HttpStatus.OK;
            assert responseEntity.getBody() == expectedResponse;
        }).verifyComplete();
        verify(userRegistrationService, times(1))
                .getUserInfo(digitalCustomerProfileId, digitalDeviceUdid);
        verify(userRegistrationService, times(1)).buildUserDetailsDTO(expectedResponse);
        verify(userInfoService, times(1)).updateFailureAttemptDetailsByUsername(userDetailsDTO);
    }

    @Test
    void getUserInfoHandlesNullResponse() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        String digitalDeviceUdid = "835185ffd672ab9c";
        when(userRegistrationService.getUserInfo(digitalCustomerProfileId, digitalDeviceUdid)).thenReturn(Mono.empty());
        Mono<ResponseEntity<UserInfoResponse>> result = userController
                .getUserInfo(digitalCustomerProfileId, digitalDeviceUdid);
        StepVerifier.create(result).verifyComplete();
        verify(userRegistrationService, times(1))
                .getUserInfo(digitalCustomerProfileId, digitalDeviceUdid);
        verify(userRegistrationService, never()).buildUserDetailsDTO(any());
        verify(userInfoService, never()).updateFailureAttemptDetailsByUsername(any());
    }

    @Test
    void getUserInfoHandlesError() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        String digitalDeviceUdid = "835185ffd672ab9c";
        RuntimeException exception = new RuntimeException("Test exception");
        when(userRegistrationService.getUserInfo(digitalCustomerProfileId, digitalDeviceUdid))
                .thenReturn(Mono.error(exception));
        Mono<ResponseEntity<UserInfoResponse>> result = userController
                .getUserInfo(digitalCustomerProfileId, digitalDeviceUdid);
        StepVerifier.create(result).verifyError(RuntimeException.class);
        verify(userRegistrationService, times(1))
                .getUserInfo(digitalCustomerProfileId, digitalDeviceUdid);
        verify(userRegistrationService, never()).buildUserDetailsDTO(any());
        verify(userInfoService, never()).updateFailureAttemptDetailsByUsername(any());
    }

    @Test
    void testGetUserPublicKey() {
        DigitalCustomerDeviceRepository digitalCustomerDeviceRepository = mock(DigitalCustomerDeviceRepository.class);
        when(digitalCustomerDeviceRepository.findByUserFaceAuthPublicKey(Mockito.any())).thenReturn(
                "By User Public Key");
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository = mock(
                DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(userInfoRepository,
                digitalCustomerProfileRepository, bankingNotificationPreferenceRepository,
                marketingNotificationPreferencesRepository,
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), digitalCustomerDeviceRepository,
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));
        UserInfoServiceImpl userInfoService = userInfoServiceImpl();
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsService = new DigitalCustomerShortcutsServiceImpl(
                mock(DigitalCustomerShortcutsRepository.class), mock(SesionHistoryRepository.class),
                mock(UserInfoRepository.class));
        DigitalCookiesPreferenceService digitalCookiesPreferenceService = mock(DigitalCookiesPreferenceService.class);
        ResponseEntity<BiometricPublicKeyResponse> actualUserPublicKey = (new UserController(userRegistrationService,
                userInfoService, pinService, digitalCustomerShortcutsService, digitalCookiesPreferenceService,
                new AuditDigitalCustomerHolder(), promotionOffers,
                digitalDeviceService,
                jsonUtils)).getUserPublicKey("Digital Device Udid", "faceId");
        verify(digitalCustomerDeviceRepository).findByUserFaceAuthPublicKey(Mockito.any());
        assertEquals(okStatus, actualUserPublicKey.getStatusCodeValue());
    }

    @Test
    void testGetUserPublicKey2() {
        UserRegistrationServiceImpl userRegistrationService = mock(UserRegistrationServiceImpl.class);
        when(userRegistrationService.getUserPublicKey(Mockito.any(), eq("faceId"))).thenReturn(
                BiometricPublicKeyResponse.builder()
                        .publicKey("User Public Key")
                        .httpStatus(HttpStatus.OK)
                        .status(SUCCESS_CODE)
                        .timeStamp(new java.util.Date())
                        .build());
        UserInfoServiceImpl userInfoService = userInfoServiceImpl();
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsService = new DigitalCustomerShortcutsServiceImpl(
                mock(DigitalCustomerShortcutsRepository.class), mock(SesionHistoryRepository.class),
                mock(UserInfoRepository.class));
        DigitalCookiesPreferenceService digitalCookiesPreferenceService = mock(DigitalCookiesPreferenceService.class);
        ResponseEntity<BiometricPublicKeyResponse> actualUserPublicKey = (new UserController(
                userRegistrationService, userInfoService,
                pinService, digitalCustomerShortcutsService, digitalCookiesPreferenceService,
                new AuditDigitalCustomerHolder(), promotionOffers, digitalDeviceService,
                jsonUtils)).getUserPublicKey(
                "Digital Device Udid", "faceId");
        verify(userRegistrationService).getUserPublicKey(Mockito.any(), eq("faceId"));
        assertEquals(okStatus, actualUserPublicKey.getStatusCodeValue());
    }

    @Test
    void testSavePublicKeyPin() {
        // Create a sample PublicKeyUpdateRequest object
        PublicKeyUpdateRequest publicKeyRequest = new PublicKeyUpdateRequest();
        publicKeyRequest.setDevicePublicKey("D5FD5D89-903F-4B4B-8B0B-49B877283B6");
        doNothing().when(userRegistrationService).saveUserPublicKeyForPin(publicKeyRequest);
        ResponseEntity<UserSuccessResponse> responseEntity = userController.savePublicKeyPin(publicKeyRequest);
        verify(userRegistrationService, times(1)).saveUserPublicKeyForPin(publicKeyRequest);
        assertEquals(okStatus, responseEntity.getStatusCodeValue());
    }

    @Test
    void testValidateUsername() {
        UserSuccessResponse userSuccessResponse = new UserSuccessResponse();
        userSuccessResponse.setMessage("User name valid");
        when(userInfoService.validateUserName(anyString())).thenReturn(userSuccessResponse);
        ResponseEntity<UserSuccessResponse> res = userController.validateUserName(anyString());
        assertEquals(ResponseEntity.ok(userSuccessResponse), res);
    }

    @Test
    void testGetUserPublicKeyForPin() {
        DigitalCustomerDeviceRepository digitalCustomerDeviceRepository = mock(DigitalCustomerDeviceRepository.class);
        when(digitalCustomerDeviceRepository.findByUserPublicKeyForPin(Mockito.any(), Mockito.any()))
                .thenReturn("By User Public Key For Pin");
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository
                = mock(DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(userInfoRepository,
                digitalCustomerProfileRepository, bankingNotificationPreferenceRepository,
                marketingNotificationPreferencesRepository,
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), digitalCustomerDeviceRepository,
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));
        UserInfoServiceImpl userInfoService = userInfoServiceImpl();
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsService = new DigitalCustomerShortcutsServiceImpl(
                mock(DigitalCustomerShortcutsRepository.class), mock(SesionHistoryRepository.class),
                mock(UserInfoRepository.class));
        DigitalCookiesPreferenceService digitalCookiesPreferenceService = mock(DigitalCookiesPreferenceService.class);
        ResponseEntity<String> actualUserPublicKeyForPin = (new UserController(userRegistrationService, userInfoService,
                pinService, digitalCustomerShortcutsService, digitalCookiesPreferenceService,
                new AuditDigitalCustomerHolder(),
                mock(PromotionOffers.class), digitalDeviceService,
                jsonUtils)).getUserPublicKeyForPin("42", "janedoe");
        verify(digitalCustomerDeviceRepository).findByUserPublicKeyForPin(Mockito.any(), Mockito.any());
        assertEquals("By User Public Key For Pin", actualUserPublicKeyForPin.getBody());
        assertEquals(okStatus, actualUserPublicKeyForPin.getStatusCodeValue());
        assertTrue(actualUserPublicKeyForPin.getHeaders().isEmpty());
    }

    @Test
    void verifyGetUserPublicKeyForPinResponse() {
        UserRegistrationService userRegistrationService = mock(UserRegistrationService.class);
        when(userRegistrationService.getUserPublicKeyForPin(Mockito.any(), Mockito.any()))
                .thenReturn("User Public Key For Pin");
        UserInfoServiceImpl userInfoService = userInfoServiceImpl();
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsService = new DigitalCustomerShortcutsServiceImpl(
                mock(DigitalCustomerShortcutsRepository.class), mock(SesionHistoryRepository.class),
                mock(UserInfoRepository.class));
        DigitalCookiesPreferenceService digitalCookiesPreferenceService = mock(DigitalCookiesPreferenceService.class);
        ResponseEntity<String> actualUserPublicKeyForPin = (new UserController(userRegistrationService, userInfoService,
                pinService, digitalCustomerShortcutsService, digitalCookiesPreferenceService,
                new AuditDigitalCustomerHolder(),
                mock(PromotionOffers.class), digitalDeviceService,
                jsonUtils)).getUserPublicKeyForPin("42", "janedoe");
        verify(userRegistrationService).getUserPublicKeyForPin(Mockito.any(), Mockito.any());
        assertEquals("User Public Key For Pin", actualUserPublicKeyForPin.getBody());
        assertEquals(okStatus, actualUserPublicKeyForPin.getStatusCodeValue());
        assertTrue(actualUserPublicKeyForPin.getHeaders().isEmpty());
    }

    @Test
    void testDeRegisteredDevices() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        List<String> customerDeviceId = Arrays.asList(DEVICE_ID1);
        DeRegisterDevicesResponse deRegisterDevicesResponse = new DeRegisterDevicesResponse();
        deRegisterDevicesResponse.setStatusCode(OK_RESPONSE_CODE);
        deRegisterDevicesResponse.setTimestamp(LocalDateTime.now());
        deRegisterDevicesResponse.setMessage("De-Registered Successfully");
        when(userInfoService.deRegisterDevices(digitalCustomerProfileId, customerDeviceId))
                .thenReturn(deRegisterDevicesResponse);

        ResponseEntity<DeRegisterDevicesResponse> response = userController
                .deRegisteredDevices(digitalCustomerProfileId, customerDeviceId);

        assertEquals(okStatus, response.getStatusCodeValue());
        assertEquals(deRegisterDevicesResponse, response.getBody());

        verify(userInfoService, times(1)).deRegisterDevices(digitalCustomerProfileId,
                customerDeviceId);
    }

    @Test
    void testGetAllDevices() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        boolean registered = true;
        final int defaultDeviceId = 42;
        List<DeviceDataForRegisterDevice> mockDevices = new ArrayList<>();
        DeviceDataForRegisterDevice mockDetail = new DeviceDataForRegisterDevice();
        mockDetail.setDigitalCustomerProfileId(digitalCustomerProfileId);
        mockDetail.setDeviceId(defaultDeviceId);
        mockDetail.setDeviceName("15 Pro");
        mockDetail.setCreationDate(Timestamp.valueOf("2024-03-27 13:30:22.58").toLocalDateTime());
        mockDetail.setModificationDate(Timestamp.valueOf("2024-09-13 14:26:25.500").toLocalDateTime());
        mockDetail.setRegisteredFlag(true);

        mockDevices.add(mockDetail);
        when(userInfoService.getAllRegisterDevice(digitalCustomerProfileId, registered)).thenReturn(mockDevices);

        List<DeviceDataForRegisterDevice> actualDevices =
                userController.getAllDevices(digitalCustomerProfileId, registered);

        assertEquals(mockDevices, actualDevices);
        verify(userInfoService, times(1)).getAllRegisterDevice(digitalCustomerProfileId,
                registered);
    }

    @Test
    void registerDeviceInfoTest() {
        // Given
        DigitalCustomerProfileDTO digitalCustomerProfileDTO = new DigitalCustomerProfileDTO();
        // set properties of digitalCustomerProfileDTO
        digitalCustomerProfileDTO.setDigitalCustomerProfileId(String.valueOf(UUID.randomUUID()));
        digitalCustomerProfileDTO.setDevicePublicKeyForPin("My Public Key");
        UserAPIBaseResponse expectedResponse = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .status("success")
                .message("Device Registration Successfully")
                .timeStamp(new Date())
                .build();
        when(userRegistrationService.saveDeviceInfo(any(DigitalCustomerProfileDTO.class)))
                .thenReturn(expectedResponse);
        UserAPIBaseResponse response = userController.registerDeviceInfo(digitalCustomerProfileDTO);
        assertEquals(expectedResponse, response);
    }


    @Test
    void testUpdatePinStatus() {
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository =
                mock(DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(userInfoRepository,
                digitalCustomerProfileRepository, bankingNotificationPreferenceRepository,
                marketingNotificationPreferencesRepository,
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));

        UserInfoServiceImpl userInfoService = userInfoServiceImpl();

        PinServiceImpl pinService = new PinServiceImpl(mock(PinRepository.class));
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsService = new DigitalCustomerShortcutsServiceImpl(
                mock(DigitalCustomerShortcutsRepository.class), mock(SesionHistoryRepository.class),
                mock(UserInfoRepository.class));

        DigitalCookiesPreferenceService digitalCookiesPreferenceService = mock(DigitalCookiesPreferenceService.class);
        UserController controller = new UserController(userRegistrationService,
                userInfoService, pinService, digitalCustomerShortcutsService,
                digitalCookiesPreferenceService, new AuditDigitalCustomerHolder(),
                mock(PromotionOffers.class), digitalDeviceService,
                jsonUtils);
        assertThrows(DigitalCustomerProfileIdNotNullException.class, () -> (controller)
                .updatePinStatus(null));

        assertThrows(DigitalCustomerProfileIdNotNullException.class,
                () -> (controller)
                        .updatePinStatus(null));
    }

    @Test
    void testFetchRules() {

        // Arrange
        UserInfoServiceImpl userInfoService = mock(UserInfoServiceImpl.class);
        when(userInfoService.getRules(Mockito.any()))
                .thenReturn(new CountryValidation("GB", "https://example.org/example", true));
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);
        DigitalCustomerProfileRepository digitalCustomerProfileRepository =
                mock(DigitalCustomerProfileRepository.class);
        BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository = mock(
                BankingNotificationPreferenceRepository.class);
        MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository = mock(
                MarketingNotificationPreferencesRepository.class);
        UserRegistrationServiceImpl userRegistrationService = new UserRegistrationServiceImpl(userInfoRepository,
                digitalCustomerProfileRepository, bankingNotificationPreferenceRepository,
                marketingNotificationPreferencesRepository,
                new DigitalCustomerProfileAndDeviceInjector(
                        mock(String.valueOf(userInfoServiceImpl())),
                        mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                        mock(String.valueOf(DigitalCustomerProfileRepository.class)),
                        mock(String.valueOf(DigitalCustomerDeviceRepository.class)),
                        mock(String.valueOf(DigitalDeviceLinkRepository.class)),
                        mock(String.valueOf(DigitalCookiePreferenceRepository.class))),
                mock(DigitalCookiePreferenceRepository.class), mock(DigitalCustomerDeviceRepository.class),
                mock(DigitalDeviceLinkRepository.class), mock(ConfigurationServiceClient.class),
                mock(NotificationUtil.class));

        PinServiceImpl pinService = new PinServiceImpl(mock(PinRepository.class));
        DigitalCustomerShortcutsServiceImpl digitalCustomerShortcutsService = new DigitalCustomerShortcutsServiceImpl(
                mock(DigitalCustomerShortcutsRepository.class), mock(SesionHistoryRepository.class),
                mock(UserInfoRepository.class));

        DigitalCookiesPreferenceService digitalCookiesPreferenceService = mock(DigitalCookiesPreferenceService.class);
        UserController userController = new UserController(userRegistrationService, userInfoService, pinService,
                digitalCustomerShortcutsService, digitalCookiesPreferenceService, new AuditDigitalCustomerHolder(),
                mock(PromotionOffers.class), digitalDeviceService,
                jsonUtils);

        // Act
        ResponseEntity<CountryValidation> actualFetchRulesResult = userController
                .fetchRules(new CountryValidation("GB", "https://example.org/example", true));

        // Assert
        verify(userInfoService).getRules(Mockito.any());
        assertEquals(number, actualFetchRulesResult.getStatusCodeValue());
        assertTrue(actualFetchRulesResult.hasBody());
        assertTrue(actualFetchRulesResult.getHeaders().isEmpty());
    }

    /**
     * Method under test: {@link UserController#checkUserPinStatus(UUID)}
     */
    @Test
    void checkUserPinStatusSuccessTest() {
        UUID profileId = UUID.randomUUID();
        boolean hasPin = true;

        when(userRegistrationService.checkUserPinStatus(profileId)).thenReturn(hasPin);
        ResponseEntity<Boolean> response = userController.checkUserPinStatus(profileId);

        verify(userRegistrationService).checkUserPinStatus(profileId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    void testUpdateBiometricStatus() {
        BiometricStatusDTO request = new BiometricStatusDTO();
        request.setFaceId(true);
        request.setTouchId(false);
        UUID digitalCustomerProfileId = UUID.randomUUID();
        String expectedResponse = "Expected Response";

        when(userInfoService.updateBiometricStatus(request, digitalCustomerProfileId)).thenReturn(expectedResponse);

        UserController userController = new UserController(userRegistrationService, userInfoService,
                pinService, digitalCustomerShortcutsService, digitalCookiesPreferenceService,
                auditDigitalCustomerIdHolder, promotionOffers, digitalDeviceService,
                jsonUtils);

        ResponseEntity<String> response = userController.updateBiometricStatus(request, digitalCustomerProfileId);

        assertEquals(expectedResponse, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetBiometricStatusForDevice() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        String customerDeviceUDID = "testDeviceUDID";

        Mono<ResponseEntity<BiometricStatusResponse>> response =
                userController.getBiometricStatusForDevice(digitalCustomerProfileId, customerDeviceUDID);
        ResponseEntity<BiometricStatusResponse> responseEntity = response.block();
        assertNotNull(responseEntity);
        BiometricStatusResponse biometricStatusResponse = responseEntity.getBody();
        assertNull(biometricStatusResponse);

        assertEquals(HttpStatus.OK, Objects.requireNonNull(response.block()).getStatusCode());
    }

    @Test
    void testGetMarketingPreferences() {
        MarketingPreferenceResponse obj = MarketingPreferenceResponse.builder()
                .marketingTypeElementName("hdrSMS")
                .marketingDescElementName("msgDealsOffers")
                .marketingFlag(true)
                .build();
        UUID digitalCustomerProfileId = UUID.randomUUID();

        List<MarketingPreferenceResponse> preferences = Arrays.asList(obj);
        when(userRegistrationService.getMarketingPreferences(digitalCustomerProfileId)).thenReturn(preferences);

        ResponseEntity<List<MarketingPreferenceResponse>> response =
                userController.getMarketingPreferences(digitalCustomerProfileId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(UdbConstants.ONE_CONSTANT, response.getBody().size());
    }

    @Test
    void testGetNotificationPreferences() {
        NotificationPreferenceResponse obj = NotificationPreferenceResponse.builder()
                .notificationTypeElementName("hdrSMS")
                .notificationDescElementName("msgSMS")
                .notificationFlag(true)
                .build();
        UUID digitalCustomerProfileId = UUID.randomUUID();

        List<NotificationPreferenceResponse> preferences = Arrays.asList(obj);
        when(userRegistrationService.getNotificationPreferences(digitalCustomerProfileId)).thenReturn(preferences);

        ResponseEntity<List<NotificationPreferenceResponse>> response =
                userController.getNotificationPreferences(digitalCustomerProfileId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(UdbConstants.ONE_CONSTANT, response.getBody().size());
    }
    private UserInfoServiceImpl userInfoServiceImpl() {
        return new UserInfoServiceImpl(digitalCustomerDeviceRepository,
                digitalDeviceLinkRepository, loginAttemptRepository,
                mock(DigitalCustomerPwdRepository.class), new AppConfig(),
                mock(UserInfoRepository.class), mock(DigitalCustomerProfileRepository.class),
                mock(String.valueOf(DigitalCustomerDeviceAuditRepository.class)),
                mock(String.valueOf(DigitalCustomerAlertService.class)),
                mock(String.valueOf(NotificationOrchestratorServiceClient.class)),
                mock(ConfigurationServiceClient.class), pinRepository,
                mock(NotificationUtil.class));
    }
}
