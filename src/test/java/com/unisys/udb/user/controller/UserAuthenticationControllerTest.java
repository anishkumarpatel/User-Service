package com.unisys.udb.user.controller;


import com.unisys.udb.user.dto.request.ComparePinRequest;
import com.unisys.udb.user.dto.request.MFARequest;
import com.unisys.udb.user.dto.request.ReAuthenticateActivityRequest;
import com.unisys.udb.user.dto.request.UpdateExpiryDTO;
import com.unisys.udb.user.dto.response.ComparePinResponse;
import com.unisys.udb.user.dto.response.CustomerInactivityPeriodResponse;
import com.unisys.udb.user.dto.response.DeviceRegistrationResponseDTO;
import com.unisys.udb.user.dto.response.DigitalCustomerDeviceResponse;
import com.unisys.udb.user.dto.response.OldPasswordHistoryResponse;
import com.unisys.udb.user.dto.response.PinHistoryResponse;
import com.unisys.udb.user.dto.response.UpdateExpiryResponse;
import com.unisys.udb.user.dto.response.UserActivityStatusResponse;
import com.unisys.udb.user.dto.response.UserLockResponse;
import com.unisys.udb.user.repository.DigitalCustomerDeviceRepository;
import com.unisys.udb.user.repository.PinHistoryRepository;
import com.unisys.udb.user.service.DigitalCustomerActivityService;
import com.unisys.udb.user.service.DigitalCustomerDeviceService;
import com.unisys.udb.user.service.MFAService;
import com.unisys.udb.user.service.PasswordHistoryService;
import com.unisys.udb.user.service.PinHistoryService;
import com.unisys.udb.user.service.PinServiceImpl;
import com.unisys.udb.user.service.ReAuthenticationService;
import com.unisys.udb.user.service.UserInfoService;
import com.unisys.udb.utility.linkmessages.dto.DynamicMessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.unisys.udb.user.constants.UdbConstants.CUSTOMER_ACTIVE_PERIOD_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.CUSTOMER_INACTIVE_PERIOD_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.OTP;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserAuthenticationControllerTest {

    @Mock
    private PasswordHistoryService passwordHistoryService;

    @Mock
    private DigitalCustomerDeviceRepository digitalCustomerDeviceRepository;

    @Mock
    private PinHistoryService pinHistoryService;

    @Mock
    private PinServiceImpl pinServiceImpl;

    @Mock
    private PinHistoryRepository pinHistoryRepository;

    @Mock
    private DigitalCustomerDeviceService digitalCustomerDeviceService;

    @Mock
    private DigitalCustomerActivityService digitalCustomerActivityService;

    @Mock
    private ReAuthenticationService reAuthenticationService;

    @InjectMocks
    private UserAuthenticationController userAuthenticationController;

    private UUID digitalCustomerProfileId;
    private OldPasswordHistoryResponse oldPasswordHistoryResponse;

    @Mock
    private UserInfoService userInfoService;

    @Mock
    private MFAService mfaService;

    @BeforeEach
    void setUp() {
        digitalCustomerProfileId = UUID.randomUUID();
        oldPasswordHistoryResponse = new OldPasswordHistoryResponse(
                Arrays.asList("password1", "password2", "password3"));
    }

    @Test
    void testGetOldPasswordsWithOldPasswords() {
        when(passwordHistoryService.fetchOldPasswords(digitalCustomerProfileId)).thenReturn(oldPasswordHistoryResponse);
        ResponseEntity<OldPasswordHistoryResponse> response = userAuthenticationController.getOldPasswords(
                digitalCustomerProfileId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(oldPasswordHistoryResponse, response.getBody());
    }

    @Test
    void testGetOldPasswordsWithNoOldPasswords() {
        OldPasswordHistoryResponse emptyResponse = new OldPasswordHistoryResponse(Collections.emptyList());
        when(passwordHistoryService.fetchOldPasswords(digitalCustomerProfileId)).thenReturn(emptyResponse);
        ResponseEntity<OldPasswordHistoryResponse> response = userAuthenticationController.getOldPasswords(
                digitalCustomerProfileId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emptyResponse, response.getBody());
    }

    @Test
    void testGetOldPinsSuccessful() {
        // Arrange
        UUID profileId = UUID.randomUUID();
        List<String> oldPins = Arrays.asList("1234", "5678");
        PinHistoryResponse expectedResponse = new PinHistoryResponse(oldPins);

        when(pinHistoryService.fetchOldPins(profileId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<PinHistoryResponse> response = userAuthenticationController.getOldPins(profileId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void testGetOldPinsNoOldPins() {
        // Arrange
        UUID profileId = UUID.randomUUID();
        PinHistoryResponse expectedResponse = new PinHistoryResponse(List.of());

        when(pinHistoryService.fetchOldPins(profileId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<PinHistoryResponse> response = userAuthenticationController.getOldPins(profileId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void testComparePinSuccessful() {
        // Arrange
        UUID profileId = UUID.randomUUID();
        String newPin = "123456";
        ComparePinRequest request = new ComparePinRequest(profileId, newPin);
        ComparePinResponse expectedResponse = new ComparePinResponse("Pin matched");

        when(pinServiceImpl.comparePin(profileId, newPin)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ComparePinResponse> response = userAuthenticationController.comparePin(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void testComparePinNotMatched() {
        // Arrange
        UUID profileId = UUID.randomUUID();
        String newPin = "123456";
        ComparePinRequest request = new ComparePinRequest(profileId, newPin);
        ComparePinResponse expectedResponse = new ComparePinResponse("Pin not matched");

        when(pinServiceImpl.comparePin(profileId, newPin)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ComparePinResponse> response = userAuthenticationController.comparePin(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void testGetDeviceInfo() throws Exception {
        DigitalCustomerDeviceResponse buildResult = DigitalCustomerDeviceResponse.builder()
                .deviceName("Device Name")
                .deviceOsVersion("1.0.2")
                .deviceType("Device Type")
                .digitalDeviceUdid("Digital Device Udid")
                .build();
        Mono<DigitalCustomerDeviceResponse> justResult = Mono.just(buildResult);
        when(digitalCustomerDeviceService.getDeviceInfo(Mockito.<String>any())).thenReturn(justResult);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.
                get("/api/v1/user/device/info/{deviceId}", "42");
        MockMvcBuilders.standaloneSetup(userAuthenticationController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().isOk());
    }

    /**
     * Method under test: {@link UserAuthenticationController#getDeviceInfo(String)}
     */
    @Test
    void testGetDeviceInfoSuccess() throws Exception {
        DigitalCustomerDeviceResponse buildResult = DigitalCustomerDeviceResponse.builder()
                .deviceName("42")
                .deviceOsVersion("1.0.2")
                .deviceType("Device Type")
                .digitalDeviceUdid("Digital Device Udid")
                .build();
        Mono<DigitalCustomerDeviceResponse> justResult = Mono.just(buildResult);
        when(digitalCustomerDeviceService.getDeviceInfo(Mockito.<String>any())).thenReturn(justResult);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/user/device/info/{deviceId}", "42");
        MockMvcBuilders.standaloneSetup(userAuthenticationController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    void testGetBroadCastMessageReference() {
        // Arrange
        UUID profileId = UUID.randomUUID();
        List<String> broadCastReferenceList = new ArrayList<>();
        broadCastReferenceList.add("1");
        when(userInfoService.getBroadCastReferenceId(profileId)).thenReturn(broadCastReferenceList);
        ResponseEntity<List<String>> response = userAuthenticationController.
                getBroadCastMessageReference(profileId);
        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(broadCastReferenceList);
    }

    @Test
    void testCheckDeviceRegistrationDeviceRegistered() {
        // Arrange
        UUID profileId = UUID.randomUUID();
        String deviceUuid = "device-uuid-123";
        DeviceRegistrationResponseDTO mockResponse = new DeviceRegistrationResponseDTO(
                true, null, null); // Device registered
        when(digitalCustomerDeviceService.checkDeviceRegistration(profileId, deviceUuid)).thenReturn(mockResponse);

        // Act
        ResponseEntity<DeviceRegistrationResponseDTO> response = userAuthenticationController.checkDeviceRegistration(
                profileId, deviceUuid);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testHasCustomerExceededInactivityPeriodInactive() {
        UUID profileId = UUID.randomUUID();
        CustomerInactivityPeriodResponse customerInactivityPeriodResponse = CustomerInactivityPeriodResponse
                .builder()
                .monthDuration("3")
                .active(false)
                .message(CUSTOMER_INACTIVE_PERIOD_MESSAGE)
                .build();
        // Arrange
        when(digitalCustomerActivityService.checkCustomerInactivityPeriod(profileId))
                .thenReturn(customerInactivityPeriodResponse);

        // Act
        ResponseEntity<CustomerInactivityPeriodResponse> response =
                userAuthenticationController.hasCustomerExceededInactivityPeriod(profileId);

        // Assert
        assertEquals(CUSTOMER_INACTIVE_PERIOD_MESSAGE, response.getBody().getMessage());
        assertFalse(response.getBody().isActive());
    }

    @Test
    void testHasCustomerExceededInactivityPeriodActive() {
        UUID profileId = UUID.randomUUID();
        CustomerInactivityPeriodResponse customerInactivityPeriodResponse = CustomerInactivityPeriodResponse
                .builder()
                .active(true)
                .message(CUSTOMER_ACTIVE_PERIOD_MESSAGE)
                .build();
        // Arrange
        when(digitalCustomerActivityService.checkCustomerInactivityPeriod(profileId))
                .thenReturn(customerInactivityPeriodResponse);

        // Act
        ResponseEntity<CustomerInactivityPeriodResponse> response =
                userAuthenticationController.hasCustomerExceededInactivityPeriod(profileId);

        // Assert
        assertEquals(CUSTOMER_ACTIVE_PERIOD_MESSAGE, response.getBody().getMessage());
        assertTrue(response.getBody().isActive());
    }

    @Test
    void testUpdateExpirySuccess() {
        // Arrange
        UpdateExpiryDTO updateExpiryDTO = new UpdateExpiryDTO();
        UpdateExpiryResponse expectedResponse = new UpdateExpiryResponse();
        when(userInfoService.updateExpiry(updateExpiryDTO)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<UpdateExpiryResponse> responseEntity =
                userAuthenticationController.updateExpiry(updateExpiryDTO);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    void testAddReAuthenticateActivity() {
        ReAuthenticateActivityRequest reAuthenticateActivityRequest = new ReAuthenticateActivityRequest();
        reAuthenticateActivityRequest.setChannel("Channel");
        reAuthenticateActivityRequest.setStatus("Status");

        String expectedResponse = "Add Re Authenticate Activity";

        when(reAuthenticationService.addReAuthenticateActivity(reAuthenticateActivityRequest))
                .thenReturn(expectedResponse);

        ResponseEntity<String> response = userAuthenticationController
                .addReAuthenticateActivity(reAuthenticateActivityRequest);

        assertEquals(ResponseEntity.ok(expectedResponse), response);
    }

    @Test
    void testLockUserAccount() {
        digitalCustomerProfileId = UUID.randomUUID();

        UserLockResponse mockResponse = new UserLockResponse();
        mockResponse.setMessage("Account locked successfully");
        mockResponse.setStatusCode(HttpStatus.OK.value());
        when(userInfoService.lockUserAccount(any(UUID.class))).thenReturn(mockResponse);

        ResponseEntity<UserLockResponse> responseEntity =
                userAuthenticationController.lockUserAccount(digitalCustomerProfileId);

        // Verify the status and the body of the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Account locked successfully", responseEntity.getBody().getMessage());
        assertEquals(HttpStatus.OK.value(), responseEntity.getBody().getStatusCode());
    }


    @Test
    void testGetUserRecentReAuthenticationActivityStatus() {
        // Arrange
        UserActivityStatusResponse userActivityStatusResponse = UserActivityStatusResponse
                .builder()
                .activityStatus("reAuth")
                .build();
        when(digitalCustomerActivityService.getUserRecentReAuthenticationActivityStatus(Mockito.any(UUID.class)))
                .thenReturn(userActivityStatusResponse);

        // Act
        ResponseEntity<UserActivityStatusResponse> response = userAuthenticationController
                .getUserRecentReAuthenticationActivityStatus(digitalCustomerProfileId);

        // Assert
        assertNotNull(response);
        assertEquals("reAuth", Objects.requireNonNull(response.getBody()).getActivityStatus());
    }

    @Test
    void testGetMFAType() {
        MFARequest mfaRequest = MFARequest.builder().deviceId("123456").mfaAction("Change Password").build();
        when(mfaService.getMFADetails(mfaRequest)).thenReturn(OTP);

        ResponseEntity<DynamicMessageResponse> response = userAuthenticationController.
                getMFAType(mfaRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
