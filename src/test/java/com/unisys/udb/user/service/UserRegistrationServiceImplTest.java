package com.unisys.udb.user.service;

import com.unisys.udb.user.constants.MessageCodesConstants;
import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.BankingNotificationPreferenceRequest;
import com.unisys.udb.user.dto.request.DigitalCustomerProfileDTO;
import com.unisys.udb.user.dto.request.MarketingNotificationPreferenceRequest;
import com.unisys.udb.user.dto.request.PublicKeyUpdateRequest;
import com.unisys.udb.user.dto.request.UserDetailDto;
import com.unisys.udb.user.dto.request.UserPublicKeyRequest;
import com.unisys.udb.user.dto.response.BiometricPublicKeyResponse;
import com.unisys.udb.user.dto.response.MarketingPreferenceResponse;
import com.unisys.udb.user.dto.response.NotificationPreferenceResponse;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.dto.response.UserInfoResponse;
import com.unisys.udb.user.dto.response.*;
import com.unisys.udb.user.entity.DigitalCookiePreference;
import com.unisys.udb.user.entity.DigitalCustomerDevice;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import com.unisys.udb.user.entity.DigitalDeviceLink;
import com.unisys.udb.user.entity.DigitalMarketingNotificationPreference;
import com.unisys.udb.user.entity.DigitalNotificationPreference;
import com.unisys.udb.user.exception.*;
import com.unisys.udb.user.repository.BankingNotificationPreferenceRepository;
import com.unisys.udb.user.repository.DigitalCookiePreferenceRepository;
import com.unisys.udb.user.repository.DigitalCustomerDeviceRepository;
import com.unisys.udb.user.repository.DigitalCustomerProfileRepository;
import com.unisys.udb.user.repository.DigitalDeviceLinkRepository;
import com.unisys.udb.user.repository.MarketingNotificationPreferencesRepository;
import com.unisys.udb.user.repository.UserInfoRepository;
import com.unisys.udb.user.service.client.ConfigurationServiceClient;
import com.unisys.udb.user.utils.dto.response.NotificationUtil;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;

import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


import static com.unisys.udb.user.constants.UdbConstants.*;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceImplTest {
    public static final int DIGITAL_BANKING_NOTIFICATION_PREFERENCE_ID = 42;
    private final int year = 1970;
    private final int maxDeviceLimit = 6;
    @InjectMocks
    private UserRegistrationServiceImpl userRegistrationServiceImpl;
    @Mock
    private DigitalCookiePreferenceRepository digitalCookiePreferenceRepository;
    @Mock
    private DigitalDeviceLinkRepository digitalDeviceLinkRepository;
    @Mock
    private BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository;
    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private DigitalCustomerProfileRepository digitalCustomerProfileRepository;
    @Mock
    private MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository;
    @Mock
    private DigitalCustomerProfileAndDeviceInjector digitalCustomerProfileAndDeviceInjector;
    @Mock
    private DigitalCustomerDeviceRepository digitalCustomerDeviceRepository;
    @Mock
    private ConfigurationServiceClient configurationServiceClient;

    @Mock
    private NotificationUtil notificationUtil;


    @Test
    void testConvertToDate() {
        Date actualConvertToDateResult = UserRegistrationServiceImpl.convertToDate(LocalDate.of(year, 1, 1));
        assertEquals("1970-01-01", (new SimpleDateFormat("yyyy-MM-dd")).format(actualConvertToDateResult));
    }
    @Test
    void testGetUserInfo3() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        String digitalDeviceUdid = "835185ffd672ab9c";
        DigitalDeviceLink digitalDeviceLink = new DigitalDeviceLink();
        DigitalCustomerDevice digitalCustomerDevice = new DigitalCustomerDevice();
        digitalCustomerDevice.setDigitalDeviceUdid(digitalDeviceUdid);
        digitalDeviceLink.setDigitalCustomerDevice(digitalCustomerDevice);
        DigitalCustomerProfile digitalCustomerProfile = new DigitalCustomerProfile();
        digitalCustomerProfile.setCoreCustomerProfileId(UUID.randomUUID());
        digitalCustomerProfile.setDigitalUserName("janedoe");
        digitalCustomerProfile.setDigitalCustomerStatusTypeId(1);

        List<DigitalDeviceLink> digitalDeviceLinks = new ArrayList<>();
        digitalDeviceLinks.add(digitalDeviceLink);
        digitalCustomerProfile.setDigitalDeviceLink(digitalDeviceLinks);

        Optional<DigitalCustomerProfile> ofResult = Optional.of(digitalCustomerProfile);

        when(userInfoRepository.existsByDigitalCustomerProfileId(digitalCustomerProfileId)).thenReturn(true);
        when(userInfoRepository.findByDigitalCustomerProfileId(digitalCustomerProfileId)).thenReturn(ofResult);

        List<String> digitalCustomerDeviceIdList = new ArrayList<>();
        digitalCustomerDeviceIdList.add(digitalDeviceUdid);
        when(digitalCustomerProfileRepository.findDigitalCustomerDeviceIdList(digitalCustomerProfileId))
                .thenReturn(digitalCustomerDeviceIdList);

        UserInfoResponse result = userRegistrationServiceImpl
                .getUserInfo(digitalCustomerProfileId, digitalDeviceUdid).block();

        assertNotNull(result);
        assertEquals("janedoe", result.getDigitalUserName());
        assertEquals(digitalCustomerProfile.getCoreCustomerProfileId(), result.getCoreCustomerProfileId());
        assertEquals(1, result.getDigitalCustomerStatusTypeId());
        assertEquals(digitalCustomerDevice.getDigitalDeviceUdid(), result.getDigitalCustomerDeviceId());

        verify(userInfoRepository).existsByDigitalCustomerProfileId(digitalCustomerProfileId);
        verify(userInfoRepository).findByDigitalCustomerProfileId(digitalCustomerProfileId);
        verify(digitalCustomerProfileRepository).findDigitalCustomerDeviceIdList(digitalCustomerProfileId);
    }



    @Test
    void testGetUserInfo4() {
        when(userInfoRepository.findByDigitalCustomerProfileId(Mockito.<UUID>any()))
                .thenThrow(new RuntimeException("Inside getUserInfo() method for the digitalCustomerProfileId: {}"));
        UUID digitalCustomerProfileId = UUID.randomUUID();
        assertThrows(RuntimeException.class, () -> userRegistrationServiceImpl
                .getUserInfo(digitalCustomerProfileId, "835185ffd672ab9c"));
        verify(userInfoRepository).findByDigitalCustomerProfileId(Mockito.<UUID>any());
    }


    @Test
    void testSaveUserAndDeviceInfoSuccess() {
        // Prepare test data
        DigitalCustomerProfileDTO digitalCustomerProfileDTO = new DigitalCustomerProfileDTO();
        digitalCustomerProfileDTO.setCoreCustomerProfileId(UUID.randomUUID().toString());
        digitalCustomerProfileDTO.setDigitalCustomerProfileId(UUID.randomUUID().toString());
        digitalCustomerProfileDTO.setDigitalUserName("testUser");
        digitalCustomerProfileDTO.setDigitalDeviceUdid("testDeviceUdid");

        // Mock repository behavior
        when(digitalCustomerProfileRepository.existsByCoreCustomerProfileId(any(UUID.class))).thenReturn(false);
        when(digitalCustomerProfileRepository.existsById(any(UUID.class))).thenReturn(false);
        doNothing().when(digitalCustomerProfileAndDeviceInjector)
                .insertDigitalProfileDeviceLink(any(DigitalCustomerProfileDTO.class));
        doNothing().when(notificationUtil).sendNotification(anyMap(), anyMap());

        // Execute the method
        StepVerifier.create(userRegistrationService
                        .saveUserAndDeviceInfo(digitalCustomerProfileDTO))
                .assertNext(response -> {
                    assertEquals(SUCCESS, response.getResponseType());
                    assertEquals(1, response.getMessages().size());
                    assertEquals(MessageCodesConstants.REGISTRATION_SUCCESS_CODE,
                            response.getMessages().get(0).getMessageCode());
                })
                .verifyComplete();
    }


    @Test
    void testSaveUserAndDeviceInfoCoreCustomerProfileIdMissing() {
        // Prepare test data with missing coreCustomerProfileId
        DigitalCustomerProfileDTO digitalCustomerProfileDTO = new DigitalCustomerProfileDTO();

        // Execute and assert
        CoreCustomerProfileEmptyException exception = assertThrows(CoreCustomerProfileEmptyException.class, () -> {
            userRegistrationService.saveUserAndDeviceInfo(digitalCustomerProfileDTO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

    }

    @Test
    void testSaveUserAndDeviceInfoCoreCustomerProfileAlreadyExists() {
        // Prepare test data
        DigitalCustomerProfileDTO digitalCustomerProfileDTO = new DigitalCustomerProfileDTO();
        digitalCustomerProfileDTO.setCoreCustomerProfileId(UUID.randomUUID().toString());

        // Mock repository behavior
        when(digitalCustomerProfileRepository.existsByCoreCustomerProfileId(any(UUID.class))).thenReturn(true);

        // Execute and assert
        CoreCustomerProfileAlreadyExistsException exception = assertThrows(
                CoreCustomerProfileAlreadyExistsException.class, () -> {
                    userRegistrationService.saveUserAndDeviceInfo(digitalCustomerProfileDTO);
                });
        assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
    }

    @Test
    void testSaveUserAndDeviceInfoNotificationFailure1() {
        // Prepare test data
        DigitalCustomerProfileDTO digitalCustomerProfileDTO = new DigitalCustomerProfileDTO();
        digitalCustomerProfileDTO.setCoreCustomerProfileId(UUID.randomUUID().toString());
        digitalCustomerProfileDTO.setDigitalCustomerProfileId(UUID.randomUUID().toString());
        digitalCustomerProfileDTO.setDigitalUserName("testUser");
        digitalCustomerProfileDTO.setDigitalDeviceUdid("testDeviceUdid");

        when(digitalCustomerProfileRepository.existsByCoreCustomerProfileId(any(UUID.class))).thenReturn(false);
        when(digitalCustomerProfileRepository.existsById(any(UUID.class))).thenReturn(false);
        doNothing().when(digitalCustomerProfileAndDeviceInjector)
                .insertDigitalProfileDeviceLink(any(DigitalCustomerProfileDTO.class));
        doThrow(new RuntimeException("Simulated notification error")).when(notificationUtil)
                .sendNotification(anyMap(), anyMap());
        assertThrows(RegistrationNotificationNotPublished.class, () -> userRegistrationService
                .saveUserAndDeviceInfo(digitalCustomerProfileDTO));
    }


    @Test
    void saveUserAndDeviceInfoWithEmptyCoreCustomerProfileId() {
        // Create a DigitalCustomerProfileDTO with empty core customer profile ID
        DigitalCustomerProfileDTO profileDTO = createDigitalCustomerProfileDTORequest();
        profileDTO.setCoreCustomerProfileId(""); // Set core customer profile ID to empty string
        assertThrows(CoreCustomerProfileEmptyException.class, () -> {
            userRegistrationServiceImpl.saveUserAndDeviceInfo(profileDTO);
        });

        verify(digitalCustomerProfileRepository, never()).existsByCoreCustomerProfileId(any(UUID.class));

        // Verify that digitalCustomerProfileRepository.existsById() is not called
        verify(digitalCustomerProfileRepository, never()).existsById(any(UUID.class));

        // Verify that digitalCustomerProfileAndDeviceInjector.insertDigitalProfileDeviceLink() is not called
        verify(digitalCustomerProfileAndDeviceInjector, never()).insertDigitalProfileDeviceLink(any());
    }

    @Test
    void saveUserAndDeviceInfoDuplicateKeyExceptionTest() {
        // Create a DigitalCustomerProfileDTO
        DigitalCustomerProfileDTO profileDTO = createDigitalCustomerProfileDTORequest();

        // Mock the repository to return true for existsById() when called with the DTO's digitalCustomerProfileId
        UUID digitalCustomerProfileId = UUID.fromString(profileDTO.getDigitalCustomerProfileId());
        when(digitalCustomerProfileRepository.existsById(digitalCustomerProfileId)).thenReturn(true);

        // Ensure that the method throws a DuplicateKeyException when called with the DTO
        DuplicationKeyException thrownException = assertThrows(DuplicationKeyException.class, () -> {
            userRegistrationServiceImpl.saveUserAndDeviceInfo(profileDTO);
        });

        // Verify that the insertDigitalProfileDeviceLink method is never called
        verify(digitalCustomerProfileAndDeviceInjector, never()).insertDigitalProfileDeviceLink(any());
    }

    @Test
    void saveUserAndDeviceInfoCoreCustomerProfileAlreadyExistsTest() {
        DigitalCustomerProfileDTO profileDTO = createDigitalCustomerProfileDTORequest();
        UUID coreCustomerProfileId = UUID.fromString(profileDTO.getCoreCustomerProfileId());
        when(digitalCustomerProfileRepository.existsByCoreCustomerProfileId(coreCustomerProfileId)).thenReturn(true);

        CoreCustomerProfileAlreadyExistsException thrownException = assertThrows(
                CoreCustomerProfileAlreadyExistsException.class, () -> {
                    userRegistrationServiceImpl.saveUserAndDeviceInfo(profileDTO);
                });

        verify(digitalCustomerProfileAndDeviceInjector, never()).insertDigitalProfileDeviceLink(any());
    }

    private DigitalCustomerProfileDTO createDigitalCustomerProfileDTORequest() {
        return DigitalCustomerProfileDTO.builder()
                .digitalCustomerProfileId("cb703f-b46b-47f9-99d8-5a997cea0a03")
                .coreCustomerProfileId("11111111-1111-1111-1111-111111111111")
                .digitalUserName("avani.vyas@unisys.com")
                .deviceName("S3 Ultra")
                .deviceType("Android")
                .digitalDeviceUdid("3F2504E0-4F89-41D3-9A0C-0305E82C3302")
                .deviceOsVersion("s3")
                .devicePublicKeyForPin("MMIIIsdfgstgwertywerty3wertwertwert")
                .build();
    }

    @Test
    void testFetchUserInfo() {
        UUID uid = UUID.randomUUID();
        when(userInfoRepository.fetchCustomerByDigitalCustomerProfileId(Mockito.<UUID>any())
        ).thenReturn(new ArrayList<>());
        when(userInfoRepository.existsByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn(true);
        assertThrows(CoreCustomerProfileIdNotFoundException.class, () -> userRegistrationServiceImpl
                .fetchUserInfo(uid));
        verify(userInfoRepository).existsByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(userInfoRepository).fetchCustomerByDigitalCustomerProfileId(Mockito.<UUID>any());
    }

    @Test
    void testFetchUserInfo4() {
        UUID uid = UUID.randomUUID();
        when(userInfoRepository.fetchCustomerByDigitalCustomerProfileId(Mockito.<UUID>any()))
                .thenReturn(new ArrayList<>() {
                    {
                        add(new Object[] {"test", "test", "test", "test", "test"});
                    }
                });
        when(userInfoRepository.existsByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn(true);
        userRegistrationServiceImpl.fetchUserInfo(uid);
        verify(userInfoRepository).existsByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(userInfoRepository).fetchCustomerByDigitalCustomerProfileId(Mockito.<UUID>any());
    }

    /**
     * Method under test: {@link UserRegistrationServiceImpl#fetchUserInfo(UUID)}
     */
    @Test
    void testFetchUserInfo2() {
        UUID uuid = UUID.randomUUID();
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        when(userInfoRepository.fetchCustomerByDigitalCustomerProfileId(Mockito.<UUID>any()))
                .thenThrow(new DigitalCustomerProfileIdNotFoundException(errorCode,
                        HttpStatus.NOT_FOUND,
                        FAILURE,
                        NOT_FOUND_ERROR_MESSAGE + uuid,
                        new ArrayList<>()));
        when(userInfoRepository.existsByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn(true);
        Assertions.assertThrows(DigitalCustomerProfileIdNotFoundException.class,
                () -> userRegistrationServiceImpl.fetchUserInfo(uuid));
        verify(userInfoRepository).existsByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(userInfoRepository).fetchCustomerByDigitalCustomerProfileId(Mockito.<UUID>any());
    }

    /**
     * Method under test: {@link UserRegistrationServiceImpl#fetchUserInfo(UUID)}
     */
    @Test
    void testFetchUserInfo3() {
        UUID uid = UUID.randomUUID();
        when(userInfoRepository.existsByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn(false);
        assertThrows(DigitalCustomerProfileIdNotFoundException.class, () -> userRegistrationServiceImpl
                .fetchUserInfo(uid));
        verify(userInfoRepository).existsByDigitalCustomerProfileId(Mockito.<UUID>any());
    }


    @Test
    void testGetBankingPreference() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        DigitalNotificationPreference digitalNotificationPreference = new DigitalNotificationPreference();
        digitalNotificationPreference.setMobilePushNotificationBanking(true);
        digitalNotificationPreference.setEmailNotificationBanking(true);
        digitalNotificationPreference.setSmsNotificationBanking(true);
        digitalNotificationPreference.setNotificationCreationDate(LocalDateTime.now());
        digitalNotificationPreference.setNotificationCreatedBy("Test User");
        digitalNotificationPreference.setNotificationModificationDate(LocalDateTime.now());
        digitalNotificationPreference.setNotificationModifiedBy("Test User");
        when(bankingNotificationPreferenceRepository.findByDigitalCustomerProfileId(any(UUID.class)))
                .thenReturn(Optional.of(digitalNotificationPreference));

        // Act
        Mono<BankingNotificationPreferenceResponse> result =
                userRegistrationServiceImpl.getBankingPreference(digitalCustomerProfileId);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(bankingPreference ->
                        bankingPreference.isMobilePushNotificationBanking() == digitalNotificationPreference
                                .isMobilePushNotificationBanking()
                                && bankingPreference.isEmailNotificationBanking() == digitalNotificationPreference
                                .isEmailNotificationBanking()
                                && bankingPreference.isSmsNotificationBanking() == digitalNotificationPreference
                                .isSmsNotificationBanking()
                                && bankingPreference.getCreatedDate().equals(digitalNotificationPreference
                                .getNotificationCreationDate())
                                && bankingPreference.getCreatedBy().equals(digitalNotificationPreference
                                .getNotificationCreatedBy())
                                && bankingPreference.getUpdatedDate().equals(digitalNotificationPreference
                                .getNotificationModificationDate())
                                && bankingPreference.getUpdatedBy().equals(digitalNotificationPreference
                                .getNotificationModifiedBy()))
                .verifyComplete();
    }



    @Test
    void testGetBankingPreferenceNotFound() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        when(bankingNotificationPreferenceRepository.findByDigitalCustomerProfileId(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act
        Mono<BankingNotificationPreferenceResponse> result =
                userRegistrationServiceImpl.getBankingPreference(digitalCustomerProfileId);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> {
                    if (throwable instanceof DigitalCustomerProfileIdNotFoundException) {
                        DigitalCustomerProfileIdNotFoundException ex = (DigitalCustomerProfileIdNotFoundException)
                                throwable;
                        return ex.getHttpStatus() == HttpStatus.NOT_FOUND
                                && ex.getErrorCode().equals(Collections.singletonList(NOT_FOUND_ERROR_CODE))
                                && ex.getErrorMessage().equals(FAILURE)
                                && ex.getErrorMessage().equals(NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId)
                                && ex.getParams().equals(Collections.singletonList(LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT))));
                    } else {
                        return false;
                    }
                });
    }


    @Test
    void testGetMarketingPreference() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        DigitalMarketingNotificationPreference marketingNotificationPreference = new
                DigitalMarketingNotificationPreference();
        marketingNotificationPreference.setMarketingEmailNotification(true);
        marketingNotificationPreference.setMarketingSmsNotification(true);
        marketingNotificationPreference.setMarketingPostNotification(true);
        marketingNotificationPreference.setMarketingTelephoneNotification(true);
        marketingNotificationPreference.setMarketingOnlineNotification(true);
        marketingNotificationPreference.setNotificationCreationDate(LocalDateTime.now());
        marketingNotificationPreference.setNotificationCreatedBy("Test User");
        marketingNotificationPreference.setNotificationModificationDate(LocalDateTime.now());
        marketingNotificationPreference.setNotificationModifiedBy("Test User");
        when(marketingNotificationPreferencesRepository.findByDigitalCustomerProfileId(any(UUID.class)))
                .thenReturn(Optional.of(marketingNotificationPreference));

        // Act
        Mono<MarketingNotificationPreferenceResponse> result =
                userRegistrationServiceImpl.getMarketingPreference(digitalCustomerProfileId);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(marketingPreference ->
                        marketingPreference.isEmailNotificationBanking() == marketingNotificationPreference
                                .isMarketingEmailNotification()
                                && marketingPreference.isSmsNotificationBanking() == marketingNotificationPreference
                                .isMarketingSmsNotification()
                                && marketingPreference.isPostNotificationMarketing() == marketingNotificationPreference
                                .isMarketingPostNotification()
                                && marketingPreference.isTelephoneNotificationMarketing()
                                == marketingNotificationPreference.isMarketingTelephoneNotification()
                                && marketingPreference.isOnlineNotificationMarketing()
                                == marketingNotificationPreference.isMarketingOnlineNotification()
                                && marketingPreference.getCreatedDate().equals(marketingNotificationPreference
                                .getNotificationCreationDate())
                                && marketingPreference.getCreatedBy().equals(marketingNotificationPreference
                                .getNotificationCreatedBy())
                                && marketingPreference.getUpdatedDate().equals(marketingNotificationPreference
                                .getNotificationModificationDate())
                                && marketingPreference.getUpdatedBy().equals(marketingNotificationPreference
                                .getNotificationModifiedBy()))
                .verifyComplete();
    }

    @Test
    void testGetMarketingPreferenceNotFound() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        when(marketingNotificationPreferencesRepository.findByDigitalCustomerProfileId(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act
        Mono<MarketingNotificationPreferenceResponse> result =
                userRegistrationServiceImpl.getMarketingPreference(digitalCustomerProfileId);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> {
                    if (throwable instanceof DigitalCustomerProfileIdNotFoundException) {
                        DigitalCustomerProfileIdNotFoundException ex = (DigitalCustomerProfileIdNotFoundException)
                                throwable;
                        return ex.getHttpStatus() == HttpStatus.NOT_FOUND
                                && ex.getErrorCode().equals(Collections.singletonList(NOT_FOUND_ERROR_CODE))
                                && ex.getErrorMessage().equals(FAILURE)
                                && ex.getErrorMessage().equals(NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId)
                                && ex.getParams().equals(Collections.singletonList(LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT))));
                    } else {
                        return false;
                    }
                });
    }




    @Test
    void testGetUserNameAndPinInfoByCustomerDeviceId() {
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        when(userInfoRepository.getUserNameInfoByCustomerDeviceId(Mockito.<Integer>any()))
                .thenThrow(new DigitalCustomerProfileIdNotFoundException(errorCode,
                        HttpStatus.NOT_FOUND,
                        FAILURE,
                        NOT_FOUND_ERROR_MESSAGE + UUID.randomUUID(),
                        new ArrayList<>()));
        assertThrows(DigitalCustomerProfileIdNotFoundException.class,
                () -> userRegistrationServiceImpl.getUserNameInfoByCustomerDeviceId(1));
        verify(userInfoRepository).getUserNameInfoByCustomerDeviceId(Mockito.<Integer>any());
    }

    @Test
    void testGetDigitalCookiePreference() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        DigitalCookiePreference digitalCookiePreference = new DigitalCookiePreference();
        digitalCookiePreference.setPerformanceCookie(true);
        digitalCookiePreference.setFunctionalCookie(true);
        digitalCookiePreference.setStrictlyAcceptanceCookie(true);
        digitalCookiePreference.setCookieCreatedBy("Test User");
        digitalCookiePreference.setCookieCreationDate(LocalDateTime.now());
        digitalCookiePreference.setCookieModifiedBy("Test User");
        digitalCookiePreference.setCookieModificationDate(LocalDateTime.now());
        when(digitalCookiePreferenceRepository.existsByDigitalCustomerProfileId(any(UUID.class)))
                .thenReturn(true);
        when(digitalCookiePreferenceRepository.findByDigitalCustomerProfileId(any(UUID.class)))
                .thenReturn(Optional.of(digitalCookiePreference));

        // Act
        Mono<DigitalCookiePreferenceResponse> result =
                userRegistrationServiceImpl.getDigitalCookiePreference(digitalCustomerProfileId);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.isPerformanceCookie() == digitalCookiePreference.isPerformanceCookie()
                                && response.isFunctionalCookie() == digitalCookiePreference.isFunctionalCookie()
                                && response.isStrictlyAcceptanceCookie() == digitalCookiePreference
                                .isStrictlyAcceptanceCookie()
                                && response.getCookieCreatedBy().equals(digitalCookiePreference
                                .getCookieCreatedBy())
                                && response.getCookieCreationDate().equals(digitalCookiePreference
                                .getCookieCreationDate())
                                && response.getCookieModifiedBy().equals(digitalCookiePreference.getCookieModifiedBy())
                                && response.getCookieModificationDate().equals(digitalCookiePreference
                                .getCookieModificationDate()))
                .verifyComplete();
    }

    @Test
    void testGetDigitalCookiePreference2() {
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        when(digitalCookiePreferenceRepository.findByDigitalCustomerProfileId(Mockito.<UUID>any()))
                .thenThrow(new DigitalCustomerProfileIdNotFoundException(errorCode,
                        HttpStatus.NOT_FOUND,
                        FAILURE,
                        NOT_FOUND_ERROR_MESSAGE + UUID.randomUUID(),
                        new ArrayList<>()));
        when(digitalCookiePreferenceRepository.existsByDigitalCustomerProfileId(
                Mockito.<UUID>any())).thenReturn(true);
        UUID uuid = UUID.randomUUID();
        assertThrows(DigitalCustomerProfileIdNotFoundException.class,
                () -> userRegistrationServiceImpl.getDigitalCookiePreference(uuid));
        verify(digitalCookiePreferenceRepository).existsByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(digitalCookiePreferenceRepository).findByDigitalCustomerProfileId(Mockito.<UUID>any());
    }

    @Test
    void testGetDigitalCookiePreference3() {
        DigitalCookiePreference.DigitalCookiePreferenceBuilder cookieCreatedByResult = DigitalCookiePreference.builder()
                .cookieCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        DigitalCookiePreference.DigitalCookiePreferenceBuilder cookieCreationDateResult = cookieCreatedByResult
                .cookieCreationDate(LocalDate.of(year, 1, 1).atStartOfDay());
        DigitalCookiePreference.DigitalCookiePreferenceBuilder digitalCookiePreferenceIdResult =
                cookieCreationDateResult
                        .cookieModificationDate(LocalDate.of(year, 1, 1).atStartOfDay())
                        .cookieModifiedBy("Jan 1, 2020 9:00am GMT+0100")
                        .digitalCookiePreferenceId(1);
        DigitalCookiePreference buildResult = digitalCookiePreferenceIdResult
                .digitalCustomerProfileId(UUID.randomUUID())
                .functionalCookie(true)
                .performanceCookie(true)
                .strictlyAcceptanceCookie(true)
                .build();
        buildResult.setCookieCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        buildResult.setCookieCreationDate(LocalDate.of(year, 1, 1).atStartOfDay());
        buildResult.setCookieModificationDate(LocalDate.of(year, 1, 1).atStartOfDay());
        buildResult.setCookieModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        buildResult.setDigitalCookiePreferenceId(1);
        buildResult.setDigitalCustomerProfileId(UUID.randomUUID());
        buildResult.setFunctionalCookie(true);
        buildResult.setPerformanceCookie(true);
        buildResult.setStrictlyAcceptanceCookie(true);
        Optional<DigitalCookiePreference> ofResult = Optional.of(buildResult);
        when(digitalCookiePreferenceRepository.findByDigitalCustomerProfileId(
                Mockito.<UUID>any())).thenReturn(ofResult);
        when(digitalCookiePreferenceRepository.existsByDigitalCustomerProfileId(
                Mockito.<UUID>any())).thenReturn(true);
        userRegistrationServiceImpl.getDigitalCookiePreference(UUID.randomUUID());
        verify(digitalCookiePreferenceRepository).existsByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(digitalCookiePreferenceRepository).findByDigitalCustomerProfileId(Mockito.<UUID>any());
    }

    @Test
    void testGetDigitalCookiePreference4() {
        when(digitalCookiePreferenceRepository.existsByDigitalCustomerProfileId(
                Mockito.<UUID>any())).thenReturn(false);
        UUID uuid = UUID.randomUUID();
        assertThrows(DigitalCustomerProfileIdNotFoundException.class,
                () -> userRegistrationServiceImpl.getDigitalCookiePreference(uuid));
        verify(digitalCookiePreferenceRepository).existsByDigitalCustomerProfileId(Mockito.<UUID>any());
    }

    @Test
    void testUpdateBankingNotificationPreference() {
        DigitalNotificationPreference bankingNotificationPreference = new DigitalNotificationPreference();
        bankingNotificationPreference.setNotificationCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        bankingNotificationPreference.setNotificationCreationDate(LocalDateTime.now());
        bankingNotificationPreference.setDigitalBankingNotificationPreferenceId(
                DIGITAL_BANKING_NOTIFICATION_PREFERENCE_ID);
        bankingNotificationPreference.setDigitalCustomerProfileId(UUID.randomUUID());
        bankingNotificationPreference.setEmailNotificationBanking(true);
        bankingNotificationPreference.setMobilePushNotificationBanking(true);
        bankingNotificationPreference.setSmsNotificationBanking(true);
        bankingNotificationPreference.setNotificationModifiedBy("2020-03-01");
        bankingNotificationPreference.setNotificationModificationDate(LocalDateTime.now());
        Optional<DigitalNotificationPreference> ofResult = Optional.of(bankingNotificationPreference);
        DigitalCustomerProfile digitalCustomerProfile = new DigitalCustomerProfile();
        Optional<DigitalCustomerProfile> result = Optional.of(digitalCustomerProfile);
        DigitalNotificationPreference bankingNotificationPreference2 = new DigitalNotificationPreference();
        bankingNotificationPreference2.setNotificationCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        bankingNotificationPreference2.setNotificationCreationDate(LocalDateTime.now());
        bankingNotificationPreference2
                .setDigitalBankingNotificationPreferenceId(DIGITAL_BANKING_NOTIFICATION_PREFERENCE_ID);
        bankingNotificationPreference2.setDigitalCustomerProfileId(UUID.randomUUID());
        bankingNotificationPreference2.setEmailNotificationBanking(true);
        bankingNotificationPreference2.setMobilePushNotificationBanking(true);
        bankingNotificationPreference2.setSmsNotificationBanking(true);
        bankingNotificationPreference2.setNotificationModifiedBy("2020-03-01");
        bankingNotificationPreference2.setNotificationModificationDate(LocalDateTime.now());
        when(bankingNotificationPreferenceRepository.saveAndFlush(Mockito.<DigitalNotificationPreference>any()))
                .thenReturn(bankingNotificationPreference2);
        when(bankingNotificationPreferenceRepository.findByDigitalCustomerProfileId(Mockito.<UUID>any()))
                .thenReturn(ofResult);
        when(digitalCustomerProfileRepository.findById(Mockito.any()))
                .thenReturn(result);
        UUID digitalCustomerProfileId = UUID.randomUUID();

        Map<String, Boolean> preferences = new HashMap<>();
        preferences.put(UDB_BANKING_PRFNC_HDR_EMAIL, true);
        preferences.put(UDB_BANKING_PRFNC_HDR_SMS, true);
        preferences.put(UDB_BANKING_PRFNC_HDR_MOBILE_PUSH, true);

        BankingNotificationPreferenceRequest request = new BankingNotificationPreferenceRequest(preferences);
        userRegistrationServiceImpl.updateBankingNotificationPreference(digitalCustomerProfileId, request);
        verify(bankingNotificationPreferenceRepository).findByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(bankingNotificationPreferenceRepository).saveAndFlush(Mockito.<DigitalNotificationPreference>any());
    }

    @Test
    void testCreateBankingNotificationPreference() {
        Optional<DigitalNotificationPreference> emptyResult = Optional.empty();
        DigitalCustomerProfile digitalCustomerProfile = new DigitalCustomerProfile();
        Optional<DigitalCustomerProfile> result = Optional.of(digitalCustomerProfile);
        DigitalNotificationPreference bankingNotificationPreference2 = new DigitalNotificationPreference();
        bankingNotificationPreference2.setNotificationCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        bankingNotificationPreference2.setNotificationCreationDate(LocalDateTime.now());
        bankingNotificationPreference2
                .setDigitalBankingNotificationPreferenceId(DIGITAL_BANKING_NOTIFICATION_PREFERENCE_ID);
        bankingNotificationPreference2.setDigitalCustomerProfileId(UUID.randomUUID());
        bankingNotificationPreference2.setEmailNotificationBanking(true);
        bankingNotificationPreference2.setMobilePushNotificationBanking(true);
        bankingNotificationPreference2.setSmsNotificationBanking(true);
        bankingNotificationPreference2.setNotificationModifiedBy("2020-03-01");
        bankingNotificationPreference2.setNotificationModificationDate(LocalDateTime.now());
        when(bankingNotificationPreferenceRepository.saveAndFlush(Mockito.<DigitalNotificationPreference>any()))
                .thenReturn(bankingNotificationPreference2);
        when(bankingNotificationPreferenceRepository.findByDigitalCustomerProfileId(Mockito.<UUID>any()))
                .thenReturn(emptyResult);
        when(digitalCustomerProfileRepository.findById(Mockito.any()))
                .thenReturn(result);
        UUID digitalCustomerProfileId = UUID.randomUUID();
        Map<String, Boolean> preferences = new HashMap<>();
        preferences.put(UDB_BANKING_PRFNC_HDR_EMAIL, true);
        preferences.put(UDB_BANKING_PRFNC_HDR_SMS, true);
        preferences.put(UDB_BANKING_PRFNC_HDR_MOBILE_PUSH, true);

        BankingNotificationPreferenceRequest request = new BankingNotificationPreferenceRequest(preferences);

        userRegistrationServiceImpl.updateBankingNotificationPreference(digitalCustomerProfileId, request);
        verify(bankingNotificationPreferenceRepository).findByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(bankingNotificationPreferenceRepository).saveAndFlush(Mockito.<DigitalNotificationPreference>any());
    }

    @Test
    void testUpdateBankingNotificationPreferenceDigitalCustomerProfileIdNotFoundException() {
        Optional<DigitalCustomerProfile> emptyResult = Optional.empty();
        when(digitalCustomerProfileRepository.findById(Mockito.any()))
                .thenReturn(emptyResult);
        UUID digitalCustomerProfileId = UUID.randomUUID();
        Map<String, Boolean> preferences = new HashMap<>();
        preferences.put(UDB_BANKING_PRFNC_HDR_EMAIL, true);
        preferences.put(UDB_BANKING_PRFNC_HDR_SMS, true);
        preferences.put(UDB_BANKING_PRFNC_HDR_MOBILE_PUSH, true);

        BankingNotificationPreferenceRequest request = new BankingNotificationPreferenceRequest(preferences);

        assertThrows(DigitalCustomerProfileIdNotFoundException.class,
                () -> userRegistrationServiceImpl.updateBankingNotificationPreference(
                        digitalCustomerProfileId, request));
    }

    @Test
    void testUpdateMarketingNotificationPreference() {
        Optional<DigitalMarketingNotificationPreference> emptyResult = Optional.empty();
        DigitalCustomerProfile digitalCustomerProfile = new DigitalCustomerProfile();
        Optional<DigitalCustomerProfile> result = Optional.of(digitalCustomerProfile);
        DigitalMarketingNotificationPreference marketingNotificationPreference2 = new
                DigitalMarketingNotificationPreference();
        marketingNotificationPreference2.setNotificationCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        marketingNotificationPreference2.setNotificationCreationDate(LocalDateTime.of(year, 1, 1, 0, 0));
        marketingNotificationPreference2.setDigitalCustomerProfileId(UUID.randomUUID());
        marketingNotificationPreference2
                .setDigitalMarketingNotificationPreferenceId(DIGITAL_BANKING_NOTIFICATION_PREFERENCE_ID);
        marketingNotificationPreference2.setMarketingEmailNotification(true);
        marketingNotificationPreference2.setMarketingOnlineNotification(true);
        marketingNotificationPreference2.setMarketingPostNotification(true);
        marketingNotificationPreference2.setMarketingSmsNotification(true);
        marketingNotificationPreference2.setMarketingTelephoneNotification(true);
        marketingNotificationPreference2.setNotificationModifiedBy("2020-03-01");
        marketingNotificationPreference2.setNotificationModificationDate(LocalDateTime.of(year, 1, 1, 0, 0));
        when(marketingNotificationPreferencesRepository.saveAndFlush(Mockito
                .<DigitalMarketingNotificationPreference>any()))
                .thenReturn(marketingNotificationPreference2);
        when(marketingNotificationPreferencesRepository.findByDigitalCustomerProfileId(Mockito.<UUID>any()))
                .thenReturn(emptyResult);
        when(digitalCustomerProfileRepository.findById(Mockito.any()))
                .thenReturn(result);
        UUID digitalCustomerProfileId = UUID.randomUUID();
        Map<String, Boolean> preferences = new HashMap<>();
        preferences.put(UDB_MARKETING_PRFNC_HDR_EMAIL, true);
        preferences.put(UDB_MARKETING_PRFNC_HDR_SMS, false);
        preferences.put(UDB_MARKETING_PRFNC_HDR_POST, true);
        preferences.put(UDB_MARKETING_PRFNC_HDR_TELEPHONE, false);
        preferences.put(UDB_MARKETING_PRFNC_HDR_ONLINE, false);

        MarketingNotificationPreferenceRequest request = new MarketingNotificationPreferenceRequest(preferences);
        userRegistrationServiceImpl.updateMarketingNotificationPreference(digitalCustomerProfileId, request);
        verify(marketingNotificationPreferencesRepository).findByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(marketingNotificationPreferencesRepository)
                .saveAndFlush(Mockito.<DigitalMarketingNotificationPreference>any());
    }

    @Test
    void testUpdateMarketingNotificationPreferenceDigitalCustomerProfileIdNotFoundException() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        MarketingNotificationPreferenceRequest request = new MarketingNotificationPreferenceRequest(new HashMap<>());
        assertThrows(DigitalCustomerProfileIdNotFoundException.class,
                () -> userRegistrationServiceImpl.updateMarketingNotificationPreference(
                        digitalCustomerProfileId, request));
    }

    @Test
    void testCheckPinExistsBasedOnDigitalDeviceId() {
        Optional<Boolean> ofResult = Optional.of(true);
        when(userInfoRepository.checkPinExistsBasedOnDigitalDeviceUdid(Mockito.<String>any())).thenReturn(ofResult);
        userRegistrationServiceImpl.checkPinExistsBasedOnDigitalDeviceId("Digital Device Udid");
        verify(userInfoRepository).checkPinExistsBasedOnDigitalDeviceUdid(Mockito.<String>any());
    }

    @Test
    void testCheckPinExistsBasedOnDigitalDeviceId2() {
        Optional<Boolean> ofResult = Optional.of(false);
        when(userInfoRepository.checkPinExistsBasedOnDigitalDeviceUdid(Mockito.<String>any())).thenReturn(ofResult);
        userRegistrationServiceImpl.checkPinExistsBasedOnDigitalDeviceId("Digital Device Udid");
        verify(userInfoRepository).checkPinExistsBasedOnDigitalDeviceUdid(Mockito.<String>any());
    }

    @Test
    void testCheckPinExistsBasedOnDigitalDeviceId3() {
        Optional<Boolean> emptyResult = Optional.empty();
        when(userInfoRepository.checkPinExistsBasedOnDigitalDeviceUdid(Mockito.<String>any())).thenReturn(emptyResult);
        assertThrows(DigitalDeviceUdidNotFoundException.class,
                () -> userRegistrationServiceImpl.checkPinExistsBasedOnDigitalDeviceId("Digital Device Udid"));
        verify(userInfoRepository).checkPinExistsBasedOnDigitalDeviceUdid(Mockito.<String>any());
    }

    @Test
    void testCheckPinExistsBasedOnDigitalDeviceId4() {
        assertThrows(InvalidDigitalDeviceUdid.class, () -> userRegistrationServiceImpl
                .checkPinExistsBasedOnDigitalDeviceId(
                        "Inside checkPinExistsBasedOnCustomerDeviceId() with digitalCustomerDeviceId: {}"));
    }

    @Test
    void testCheckPinExistsBasedOnDigitalDeviceId5() {
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        when(userInfoRepository.checkPinExistsBasedOnDigitalDeviceUdid(Mockito.<String>any()))
                .thenThrow(new DigitalCustomerProfileIdNotFoundException(errorCode,
                        HttpStatus.NOT_FOUND,
                        FAILURE,
                        NOT_FOUND_ERROR_MESSAGE + UUID.randomUUID(),
                        new ArrayList<>()));
        assertThrows(DigitalCustomerProfileIdNotFoundException.class,
                () -> userRegistrationServiceImpl.checkPinExistsBasedOnDigitalDeviceId("Digital Device Udid"));
        verify(userInfoRepository).checkPinExistsBasedOnDigitalDeviceUdid(Mockito.<String>any());
    }

    @Test
    void testSaveUserPublicKeyForBioMetric() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserPublicKeyRequest userPublicKeyRequest = new UserPublicKeyRequest();
        userPublicKeyRequest.setDeviceUUID("deviceUUID");
        userPublicKeyRequest.setDevicePublicKey("devicePublicKey");
        userPublicKeyRequest.setBiometricType("faceId");
        userPublicKeyRequest.setBiometricEnable(true);
        DigitalCustomerDevice device = new DigitalCustomerDevice();
        device.setDeviceFacePublicKey(null);

        when(digitalCustomerDeviceRepository.findDigitalCustomerProfileId(anyString()))
                .thenReturn(digitalCustomerProfileId);
        when(digitalCustomerDeviceRepository.getDigitalCustomerDeviceByDeviceUUIDAndProfileID(String.valueOf(
                digitalCustomerProfileId), userPublicKeyRequest.getDeviceUUID()))
                .thenReturn(Optional.of(device));

        UserAPIBaseResponse response = userRegistrationServiceImpl
                .saveUserPublicKeyForBioMetric(digitalCustomerProfileId,
                        userPublicKeyRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Public Key Saved Successfully", response.getMessage());
        verify(digitalCustomerDeviceRepository, times(1)).saveAndFlush(device);
    }

    @Test
    void testSaveUserPublicKeyForBioMetricUserPublicKeyAlreadyExistException() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserPublicKeyRequest userPublicKeyRequest = new UserPublicKeyRequest();
        userPublicKeyRequest.setDeviceUUID("deviceUUID");
        userPublicKeyRequest.setDevicePublicKey("devicePublicKey");
        userPublicKeyRequest.setBiometricType("faceId");
        userPublicKeyRequest.setBiometricEnable(true);
        DigitalCustomerDevice device = new DigitalCustomerDevice();
        device.setDeviceFacePublicKey("existingPublicKey");

        when(digitalCustomerDeviceRepository.findDigitalCustomerProfileId(anyString()))
                .thenReturn(digitalCustomerProfileId);
        when(digitalCustomerDeviceRepository.getDigitalCustomerDeviceByDeviceUUIDAndProfileID(String.valueOf(
                        digitalCustomerProfileId),
                userPublicKeyRequest.getDeviceUUID())).thenReturn(Optional.of(device));


        // Act and Assert
        assertThrows(UserPublicKeyAlreadyExistException.class, () -> {
            userRegistrationServiceImpl.saveUserPublicKeyForBioMetric(digitalCustomerProfileId, userPublicKeyRequest);
        });
    }

    @Test
    void testSaveUserPublicKeyForBioMetricDigitalCustomerProfileIdNotFoundException() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        UserPublicKeyRequest userPublicKeyRequest = new UserPublicKeyRequest();
        userPublicKeyRequest.setDeviceUUID("deviceUUID");
        userPublicKeyRequest.setDevicePublicKey("devicePublicKey");

        UUID differentDigitalCustomerProfileId = UUID.randomUUID();

        when(digitalCustomerDeviceRepository.findDigitalCustomerProfileId(anyString()))
                .thenReturn(differentDigitalCustomerProfileId);

        // Act and Assert
        assertThrows(UserDeviceNotLinkedException.class, () -> {
            userRegistrationServiceImpl.saveUserPublicKeyForBioMetric(digitalCustomerProfileId, userPublicKeyRequest);
        });
    }

    @Test
    void testSaveUserPublicKeyForBioMetricPublicKeyAlreadyExists() {
        // Arrange
        UserPublicKeyRequest userPublicKeyRequest = new UserPublicKeyRequest();
        userPublicKeyRequest.setDeviceUUID(UUID.randomUUID().toString());
        userPublicKeyRequest.setDevicePublicKey("publicKey");

        DigitalCustomerDevice device = new DigitalCustomerDevice();
        device.setDeviceFacePublicKey("publicKey");
        UUID digitalCustomerProfileId = UUID.randomUUID();
        assertThrows(UserDeviceNotLinkedException.class, () -> {
            userRegistrationServiceImpl.saveUserPublicKeyForBioMetric(digitalCustomerProfileId, userPublicKeyRequest);
        });
    }

    @Test
    void whenDeviceUdidIsNullThenThrowsIllegalArgumentException() {
        assertThrows(UserPublicKeyNotFoundException.class, () -> userRegistrationServiceImpl
                .getUserPublicKey(null, "faceId"));
    }
    @Test
    void whenPublicKeyNotFoundThenThrowsUserPublicKeyNotFoundException() {
        when(digitalCustomerDeviceRepository.findByUserFaceAuthPublicKey(anyString())).thenReturn(null);
        assertThrows(UserPublicKeyNotFoundException.class, () -> userRegistrationServiceImpl
                .getUserPublicKey("testUdid", "faceId"));
    }

    @Test
    void whenDatabaseOperationFailsThenThrowsDatabaseOperationsException() {
        when(digitalCustomerDeviceRepository.findByUserFaceAuthPublicKey(anyString()))
                .thenThrow(DatabaseOperationsException.class);
        assertThrows(DatabaseOperationsException.class, () -> userRegistrationServiceImpl
                .getUserPublicKey("testUdid", "faceId"));
    }

    @Test
    void whenPublicKeyFoundThenReturnPublicKey() {
        BiometricPublicKeyResponse expectedPublicKey = BiometricPublicKeyResponse.builder()
                .publicKey("Test public key")
                .status("200")
                .timeStamp(new java.util.Date())
                .httpStatus(HttpStatus.OK)
                .build();

        when(digitalCustomerDeviceRepository.findByUserFaceAuthPublicKey(anyString())).thenReturn("Test public key");

        BiometricPublicKeyResponse actualPublicKey = userRegistrationServiceImpl
                .getUserPublicKey("testUdid", "faceId");

        assertEquals(expectedPublicKey.getPublicKey(), actualPublicKey.getPublicKey());
    }


    @InjectMocks
    private UserRegistrationServiceImpl userRegistrationService;

    @Test
    void buildUserDetailsDTOReturnsExpectedUserDetailDto() {
        // Arrange
        UserInfoResponse userInfoResponse = UserInfoResponse.builder()
                .digitalUserName("john.doe@email.com")
                .digitalCustomerDeviceId("ABC123")
                .build();

        // Act
        UserDetailDto result = userRegistrationService.buildUserDetailsDTO(userInfoResponse);

        // Assert
        assertNotNull(result);
        assertEquals("john.doe@email.com", result.getUserName());
        assertEquals("success", result.getUserMessage());
        assertEquals("ABC123", result.getDigitalCustomerDeviceId());
    }

    @Test
    void buildUserDetailsDTOIgnoresDigitalCustomerDeviceId() {
        // Arrange
        UserInfoResponse userInfoResponse = UserInfoResponse.builder()
                .digitalUserName("john.doe@email.com")
                .digitalCustomerDeviceId("ABC123")
                .digitalCustomerProfileId(UUID.randomUUID())
                .coreCustomerProfileId(UUID.randomUUID())
                .digitalCustomerStatusTypeId(1)
                .build();

        // Act
        UserDetailDto result = userRegistrationService.buildUserDetailsDTO(userInfoResponse);

        // Assert
        assertNotNull(result);
        assertEquals("john.doe@email.com", result.getUserName());
        assertEquals("success", result.getUserMessage());
        assertEquals("ABC123", result.getDigitalCustomerDeviceId());
    }

    @Test
    void testCheckMfaStatusBasedOnDigitalDeviceId() {
        Optional<Boolean> ofResult = Optional.of(true);
        when(userInfoRepository.checkMfaStatusBasedOnDigitalDeviceUdid(Mockito.<String>any())).thenReturn(ofResult);
        userRegistrationServiceImpl.checkMfaStatusBasedOnDigitalDeviceId("Digital Device Udid");
        verify(userInfoRepository).checkMfaStatusBasedOnDigitalDeviceUdid(Mockito.<String>any());
    }

    @Test
    void testCheckMfaStatusBasedOnDigitalDeviceId2() {
        Optional<Boolean> ofResult = Optional.of(false);
        when(userInfoRepository.checkMfaStatusBasedOnDigitalDeviceUdid(Mockito.<String>any())).thenReturn(ofResult);
        userRegistrationServiceImpl.checkMfaStatusBasedOnDigitalDeviceId("Digital Device Udid");
        verify(userInfoRepository).checkMfaStatusBasedOnDigitalDeviceUdid(Mockito.<String>any());
    }

    @Test
    void testCheckMfaStatusBasedOnDigitalDeviceId3() {
        Optional<Boolean> emptyResult = Optional.empty();
        when(userInfoRepository.checkMfaStatusBasedOnDigitalDeviceUdid(Mockito.<String>any())).thenReturn(emptyResult);
        assertThrows(DigitalDeviceUdidNotFoundException.class,
                () -> userRegistrationServiceImpl.checkMfaStatusBasedOnDigitalDeviceId("Digital Device Udid"));
        verify(userInfoRepository).checkMfaStatusBasedOnDigitalDeviceUdid(Mockito.<String>any());
    }


    @Test
    void testCheckMfaStatusBasedOnDigitalDeviceId4() {
        assertThrows(InvalidDigitalDeviceUdid.class, () -> userRegistrationServiceImpl.
                checkMfaStatusBasedOnDigitalDeviceId(
                        "Inside checkMfaStatusBasedOnDigitalDeviceId() with digitalCustomerDeviceId: {}"));
    }

    @Test
    void testCheckMfaStatusBasedOnDigitalDeviceId5() {
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        when(userInfoRepository.checkMfaStatusBasedOnDigitalDeviceUdid(Mockito.<String>any()))
                .thenThrow(new DigitalCustomerProfileIdNotFoundException(errorCode,
                        HttpStatus.NOT_FOUND,
                        FAILURE,
                        NOT_FOUND_ERROR_MESSAGE + UUID.randomUUID(),
                        new ArrayList<>()));
        assertThrows(DigitalCustomerProfileIdNotFoundException.class,
                () -> userRegistrationServiceImpl.checkMfaStatusBasedOnDigitalDeviceId("Digital Device Udid"));
        verify(userInfoRepository).checkMfaStatusBasedOnDigitalDeviceUdid(Mockito.<String>any());
    }

    /**
     * Method under test:
     * {@link UserRegistrationServiceImpl#saveUserPublicKeyForPin(PublicKeyUpdateRequest)}
     */
    @Test
    void testSaveUserPublicKeyForPin() {

        DigitalCustomerDevice device = new DigitalCustomerDevice();
        DigitalCustomerProfile digitalCustomerProfile =
                new DigitalCustomerProfile();


        when(digitalCustomerDeviceRepository.findByDigitalCustomerProfileIdAndDigitalDeviceUdid(any(), any()))
                .thenReturn(device);
        when(userInfoRepository.findByDigitalCustomerProfileId(any()))
                .thenReturn(Optional.of(digitalCustomerProfile));


        PublicKeyUpdateRequest publicKeyRequest = createSamplePublicKeyUpdateRequest();

        userRegistrationServiceImpl.saveUserPublicKeyForPin(publicKeyRequest);
        verify(digitalCustomerDeviceRepository, times(1)).save(device);
        verify(userInfoRepository, times(1)).save(digitalCustomerProfile);
    }

    /**
     * Method under test:
     * {@link UserRegistrationServiceImpl#saveUserPublicKeyForPin(PublicKeyUpdateRequest)}
     */
    @Test
    void testSaveUserPublicKeyForPinWhenPublicKeyAlreadyExists() {
        DigitalCustomerDevice device = new DigitalCustomerDevice();
        device.setDevicePinPublicKey("Device Public Key");
        when(digitalCustomerDeviceRepository.findByDigitalCustomerProfileIdAndDigitalDeviceUdid(any(), any()))
                .thenReturn(device);

        PublicKeyUpdateRequest publicKeyRequest = createSamplePublicKeyUpdateRequest();

        assertThrows(UserPublicKeyAlreadyExistException.class,
                () -> userRegistrationServiceImpl.saveUserPublicKeyForPin(publicKeyRequest));
    }

    /**
     * Method under test:
     * {@link UserRegistrationServiceImpl#saveUserPublicKeyForPin(PublicKeyUpdateRequest)}
     */
    @Test
    void testSaveUserPublicKeyForPinWhenDeviceDoesNotExist() {
        when(digitalCustomerDeviceRepository.findByDigitalCustomerProfileIdAndDigitalDeviceUdid(any(), any()))
                .thenReturn(null);

        PublicKeyUpdateRequest publicKeyRequest = createSamplePublicKeyUpdateRequest();

        assertThrows(DigitalCustomerDeviceNotFoundException.class,
                () -> userRegistrationServiceImpl.saveUserPublicKeyForPin(publicKeyRequest));
    }

    public PublicKeyUpdateRequest createSamplePublicKeyUpdateRequest() {
        PublicKeyUpdateRequest publicKeyRequest = new PublicKeyUpdateRequest();
        publicKeyRequest.setDeviceUdid("01234567-89AB-CDEF-FEDC-BA9876543210");
        publicKeyRequest.setDigitalCustomerProfile(UUID.randomUUID());
        publicKeyRequest.setDevicePublicKey("publicKey");
        return publicKeyRequest;
    }
    @Test
    void testGetUserPublicKeyForPin() {
        when(digitalCustomerDeviceRepository.findByUserPublicKeyForPin(Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn("By User Public Key For Pin");
        String actualUserPublicKeyForPin = userRegistrationServiceImpl.getUserPublicKeyForPin("42", "janedoe");
        verify(digitalCustomerDeviceRepository).findByUserPublicKeyForPin(Mockito.<String>any(), Mockito.<String>any());
        assertEquals("By User Public Key For Pin", actualUserPublicKeyForPin);
    }
    @Test
    void shouldThrowExceptionWhenUserPublicKeyForPinNotFound() {
        when(digitalCustomerDeviceRepository.findByUserPublicKeyForPin(Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn("");
        assertThrows(UserPublicKeyNotFoundException.class,
                () -> userRegistrationServiceImpl.getUserPublicKeyForPin("42", "janedoe"));
        verify(digitalCustomerDeviceRepository).findByUserPublicKeyForPin(Mockito.<String>any(), Mockito.<String>any());
    }
    @Test
    void shouldThrowDigitalCustomerProfileIdNotFoundExceptionWhenUserPublicKeyForPinNotFound() {
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        when(digitalCustomerDeviceRepository.findByUserPublicKeyForPin(Mockito.<String>any(), Mockito.<String>any()))
                .thenThrow(new DigitalCustomerProfileIdNotFoundException(errorCode,
                        HttpStatus.NOT_FOUND,
                        FAILURE,
                        NOT_FOUND_ERROR_MESSAGE + UUID.randomUUID(),
                        new ArrayList<>()));
        assertThrows(DigitalCustomerProfileIdNotFoundException.class,
                () -> userRegistrationServiceImpl.getUserPublicKeyForPin("42", "janedoe"));
        verify(digitalCustomerDeviceRepository).findByUserPublicKeyForPin(Mockito.<String>any(), Mockito.<String>any());
    }
    @Test
    void testUpdatePinStatus() {
        DigitalDeviceLink digitalDeviceLink = new DigitalDeviceLink();
        digitalDeviceLink.setDeviceLinkCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalDeviceLink.setDeviceLinkCreationDate(LocalDate.now().atStartOfDay());
        digitalDeviceLink.setDeviceLinkModificationDate(LocalDate.now().atStartOfDay());
        digitalDeviceLink.setDeviceLinkModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalDeviceLink.setDigitalCustomerDevice(new DigitalCustomerDevice());
        digitalDeviceLink.setDigitalCustomerProfile(new DigitalCustomerProfile());
        digitalDeviceLink.setDigitalDeviceLinkId(1);

        DigitalCustomerDevice digitalCustomerDevice = new DigitalCustomerDevice();
        digitalCustomerDevice.setDeviceCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerDevice.setDeviceCreationDate(LocalDate.now().atStartOfDay());
        digitalCustomerDevice.setDeviceFacePublicKey("Device Face Public Key");
        digitalCustomerDevice.setDeviceModificationDate(LocalDate.now().atStartOfDay());
        digitalCustomerDevice.setDeviceModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCustomerDevice.setDeviceName("Device Name");
        digitalCustomerDevice.setDeviceOsVersion("1.0.2");
        digitalCustomerDevice.setDevicePinPublicKey("Device Pin Public Key");
        digitalCustomerDevice.setDeviceStatus(true);
        digitalCustomerDevice.setDeviceToken("ABC123");
        digitalCustomerDevice.setDeviceType("Device Type");
        digitalCustomerDevice.setDigitalCustomerDeviceId(1);
        digitalCustomerDevice.setDigitalDeviceLink(digitalDeviceLink);
        digitalCustomerDevice.setDigitalDeviceUdid("Digital Device Udid");
        digitalCustomerDevice.setFunctionalCookie(true);
        digitalCustomerDevice.setPerformanceCookie(true);
        digitalCustomerDevice.setStrictlyAcceptanceCookie(true);
        digitalCustomerDevice.setTermsAndConditions(true);

        DigitalDeviceLink digitalDeviceLink2 = new DigitalDeviceLink();
        digitalDeviceLink2.setDeviceLinkCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalDeviceLink2.setDeviceLinkCreationDate(LocalDate.now().atStartOfDay());
        digitalDeviceLink2.setDeviceLinkModificationDate(LocalDate.now().atStartOfDay());
        digitalDeviceLink2.setDeviceLinkModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalDeviceLink2.setDigitalCustomerDevice(new DigitalCustomerDevice());
        digitalDeviceLink2.setDigitalCustomerProfile(new DigitalCustomerProfile());
        digitalDeviceLink2.setDigitalDeviceLinkId(1);

        DigitalCustomerProfile digitalCustomerProfile = new DigitalCustomerProfile();
        digitalCustomerProfile.setDigitalAccountStatusReason("Just cause");
        digitalCustomerProfile.setDigitalCustomerStatusTypeId(1);

        List<DigitalDeviceLink> digitalDeviceLinks2 = new ArrayList<>();
        digitalDeviceLinks2.add(digitalDeviceLink2);
        digitalCustomerProfile.setDigitalDeviceLink(digitalDeviceLinks2);

        digitalCustomerProfile.setDigitalUserName("janedoe");
        digitalCustomerProfile.setMfaActivityCompleted(true);
        digitalCustomerProfile.setPinSetCompleted(true);
        digitalCustomerProfile.setProfileCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerProfile.setProfileModificationDate(LocalDate.now().atStartOfDay());
        digitalCustomerProfile.setProfileModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCustomerProfile.setRegistrationDate(LocalDate.now().atStartOfDay());

        DigitalDeviceLink digitalDeviceLink3 = new DigitalDeviceLink();
        digitalDeviceLink3.setDeviceLinkCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalDeviceLink3.setDeviceLinkCreationDate(LocalDate.now().atStartOfDay());
        digitalDeviceLink3.setDeviceLinkModificationDate(LocalDate.now().atStartOfDay());
        digitalDeviceLink3.setDeviceLinkModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalDeviceLink3.setDigitalCustomerDevice(digitalCustomerDevice);
        digitalDeviceLink3.setDigitalCustomerProfile(digitalCustomerProfile);
        digitalDeviceLink3.setDigitalDeviceLinkId(1);

        DigitalCustomerProfile digitalCustomerProfile2 = new DigitalCustomerProfile();
        digitalCustomerProfile2.setDigitalAccountStatusReason("Just cause");
        digitalCustomerProfile2.setDigitalCustomerStatusTypeId(1);

        List<DigitalDeviceLink> digitalDeviceLinks3 = new ArrayList<>();
        digitalDeviceLinks3.add(digitalDeviceLink3);
        digitalCustomerProfile2.setDigitalDeviceLink(digitalDeviceLinks3);

        digitalCustomerProfile2.setDigitalUserName("janedoe");
        digitalCustomerProfile2.setMfaActivityCompleted(true);
        digitalCustomerProfile2.setPinSetCompleted(true);
        digitalCustomerProfile2.setProfileCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerProfile2.setProfileModificationDate(LocalDate.now().atStartOfDay());
        digitalCustomerProfile2.setProfileModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCustomerProfile2.setRegistrationDate(LocalDate.now().atStartOfDay());

        Optional<DigitalCustomerProfile> ofResult = Optional.of(digitalCustomerProfile2);
        when(userInfoRepository.findByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn(ofResult);
        userRegistrationServiceImpl.updatePinStatus(null, true);
        verify(userInfoRepository).findByDigitalCustomerProfileId(Mockito.<UUID>any());
    }


    @Test
    void testCheckUserPinStatusWhenPinExist() throws Exception {
        UUID profileId = UUID.randomUUID();
        boolean hasPin = true;

        when(digitalCustomerProfileRepository.findPinSetCompletedByProfileId(Mockito.any())).thenReturn(hasPin);
        Boolean pinStatus = userRegistrationServiceImpl.checkUserPinStatus(profileId);

        assertTrue(pinStatus);
        verify(digitalCustomerProfileRepository).findPinSetCompletedByProfileId(profileId);
    }

    @Test
    void testCheckUserPinStatusWhenEmptyOptional() throws Exception {
        UUID profileId = UUID.randomUUID();

        when(digitalCustomerProfileRepository.findPinSetCompletedByProfileId(profileId)).thenReturn(null);

        assertThrows(DigitalCustomerProfileIdNotNullException.class, () -> {
            userRegistrationServiceImpl.checkUserPinStatus(profileId);
        });
    }

    @Test
    void testCheckUserPinStatusWhenDataAccessException() throws Exception {
        UUID profileId = UUID.randomUUID();

        when(digitalCustomerProfileRepository.findPinSetCompletedByProfileId(profileId))
                .thenThrow(new DataAccessException("Database access error") {
                });

        assertThrows(DatabaseOperationsException.class, () -> {
            userRegistrationServiceImpl.checkUserPinStatus(profileId);
        });
    }

    @Test
    void testSaveUserPublicKeyForBioMetricTouchIdDisabled() {

        UUID digitalCustomerProfileId = UUID.randomUUID();
        String deviceUUID = "deviceUUID";
        UserPublicKeyRequest userPublicKeyRequest = new UserPublicKeyRequest();
        userPublicKeyRequest.setDeviceUUID(deviceUUID);
        userPublicKeyRequest.setBiometricType("touchID");
        userPublicKeyRequest.setBiometricEnable(false);

        DigitalCustomerDevice device = new DigitalCustomerDevice();
        device.setDeviceTouchPublicKey("existing_public_key");

        when(digitalCustomerDeviceRepository.findDigitalCustomerProfileId(deviceUUID)).thenReturn(
                digitalCustomerProfileId);
        when(digitalCustomerDeviceRepository.getDigitalCustomerDeviceByDeviceUUIDAndProfileID(
                String.valueOf(digitalCustomerProfileId), deviceUUID))
                .thenReturn(Optional.of(device));


        UserAPIBaseResponse response = userRegistrationServiceImpl.saveUserPublicKeyForBioMetric(
                digitalCustomerProfileId,
                userPublicKeyRequest);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Biometric Disabled", response.getMessage());
        assertNull(device.getDeviceTouchPublicKey());
        verify(digitalCustomerDeviceRepository, times(1)).saveAndFlush(device);
    }

    @Test
    void testSaveUserPublicKeyForBioMetricFaceIdDisabled() {

        UUID digitalCustomerProfileId = UUID.randomUUID();
        String deviceUUID = "deviceUUID";
        UserPublicKeyRequest userPublicKeyRequest = new UserPublicKeyRequest();
        userPublicKeyRequest.setDeviceUUID(deviceUUID);
        userPublicKeyRequest.setBiometricType("faceID");
        userPublicKeyRequest.setBiometricEnable(false);

        DigitalCustomerDevice device = new DigitalCustomerDevice();
        device.setDeviceTouchPublicKey("existing_public_key");

        when(digitalCustomerDeviceRepository.findDigitalCustomerProfileId(deviceUUID)).thenReturn(
                digitalCustomerProfileId);
        when(digitalCustomerDeviceRepository.getDigitalCustomerDeviceByDeviceUUIDAndProfileID(
                String.valueOf(digitalCustomerProfileId), deviceUUID))
                .thenReturn(Optional.of(device));


        UserAPIBaseResponse response = userRegistrationServiceImpl.saveUserPublicKeyForBioMetric(
                digitalCustomerProfileId,
                userPublicKeyRequest);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Biometric Disabled", response.getMessage());
        assertNull(device.getDeviceFacePublicKey());
        verify(digitalCustomerDeviceRepository, times(1)).saveAndFlush(device);
    }

    @Test
    void testGetMarketingPreferencesWithUserPreferences() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        Optional<DigitalMarketingNotificationPreference> userPreference = Optional.of(createMockUserPreference());
        List<MarketingPreferenceResponse> defaultPreferences = createMockDefaultPreferences();

        // Mock repository to return user preferences
        when(marketingNotificationPreferencesRepository.findByDigitalCustomerProfileId(digitalCustomerProfileId))
                .thenReturn(userPreference);
        when(configurationServiceClient.getDefaultMarketingPreferences()).thenReturn(defaultPreferences);

        // Call service method
        List<MarketingPreferenceResponse> actualPreferences =
                userRegistrationServiceImpl.getMarketingPreferences(digitalCustomerProfileId);

        // Verify method calls
        verify(marketingNotificationPreferencesRepository).findByDigitalCustomerProfileId(digitalCustomerProfileId);
        verify(configurationServiceClient).getDefaultMarketingPreferences();
        assertNotNull(actualPreferences);
        assertEquals(defaultPreferences.size(), actualPreferences.size()); // Check size of returned preferences
    }

    @Test
    void testGetMarketingPreferencesWithoutUserPreferences() throws Exception {
        // Prepare test data
        UUID digitalCustomerProfileId = UUID.randomUUID();
        Optional<DigitalMarketingNotificationPreference> userPreference = Optional.empty();
        List<MarketingPreferenceResponse> defaultPreferences = createMockDefaultPreferences();

        // Mock repository to return empty user preferences
        Mockito.when(marketingNotificationPreferencesRepository
                .findByDigitalCustomerProfileId(digitalCustomerProfileId)).thenReturn(userPreference);
        Mockito.when(configurationServiceClient.getDefaultMarketingPreferences())
                .thenReturn(defaultPreferences);

        List<MarketingPreferenceResponse> actualPreferences =
                userRegistrationServiceImpl.getMarketingPreferences(digitalCustomerProfileId);

        Mockito.verify(marketingNotificationPreferencesRepository)
                .findByDigitalCustomerProfileId(digitalCustomerProfileId);
        Mockito.verify(configurationServiceClient).getDefaultMarketingPreferences();
        assertNotNull(actualPreferences);
        assertEquals(defaultPreferences.size(), actualPreferences.size()); // Check size of returned preferences
    }

    @Test
    void testGetMarketingPreferencesWebClientResponseException() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        when(marketingNotificationPreferencesRepository.findByDigitalCustomerProfileId(digitalCustomerProfileId))
                .thenThrow(WebClientResponseException.class);

        assertThrows(WebClientResponseException.class, () -> {
            userRegistrationServiceImpl.getMarketingPreferences(digitalCustomerProfileId);
        });

        verify(marketingNotificationPreferencesRepository).findByDigitalCustomerProfileId(digitalCustomerProfileId);
        verify(configurationServiceClient, never()).getDefaultMarketingPreferences();
    }

    @Test
    void testGetMarketingPreferencesGenericException() throws Exception {
        UUID digitalCustomerProfileId = UUID.randomUUID();

        // Mock repository to throw generic Exception
        Mockito.when(marketingNotificationPreferencesRepository
                        .findByDigitalCustomerProfileId(digitalCustomerProfileId))
                .thenThrow(new RuntimeException("Some error"));

        assertThrows(MarketingPreferenceException.class, () -> {
            userRegistrationServiceImpl.getMarketingPreferences(digitalCustomerProfileId);
        });

        Mockito.verify(marketingNotificationPreferencesRepository)
                .findByDigitalCustomerProfileId(digitalCustomerProfileId);
        Mockito.verify(configurationServiceClient, never()).getDefaultMarketingPreferences();
    }

    private DigitalMarketingNotificationPreference createMockUserPreference() {
        return DigitalMarketingNotificationPreference.builder()
                .digitalMarketingNotificationPreferenceId(1)
                .digitalCustomerProfileId(UUID.randomUUID())
                .marketingEmailNotification(true)
                .marketingSmsNotification(true)
                .marketingPostNotification(false)
                .marketingTelephoneNotification(false)
                .marketingOnlineNotification(true)
                .notificationCreationDate(LocalDateTime.now())
                .notificationCreatedBy("test")
                .build();
    }

    private List<MarketingPreferenceResponse> createMockDefaultPreferences() {
        List<MarketingPreferenceResponse> defaultPreferences = new ArrayList<>();
        defaultPreferences.add(MarketingPreferenceResponse.builder()
                .marketingTypeElementName("hdrEmail")
                .marketingDescElementName("Email Marketing")
                .marketingFlag(true)
                .build());
        defaultPreferences.add(MarketingPreferenceResponse.builder()
                .marketingTypeElementName("hdrSMS")
                .marketingDescElementName("SMS Marketing")
                .marketingFlag(true)
                .build());
        defaultPreferences.add(MarketingPreferenceResponse.builder()
                .marketingTypeElementName("hdrPost")
                .marketingDescElementName("POST Marketing")
                .marketingFlag(true)
                .build());
        defaultPreferences.add(MarketingPreferenceResponse.builder()
                .marketingTypeElementName("hdrTelephone")
                .marketingDescElementName("Telephone Marketing")
                .marketingFlag(true)
                .build());
        defaultPreferences.add(MarketingPreferenceResponse.builder()
                .marketingTypeElementName("hdrOnline")
                .marketingDescElementName("Online Marketing")
                .marketingFlag(true)
                .build());
        return defaultPreferences;
    }

    @Test
    void testGetNotificationPreferencesWithUserPreferences() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        Optional<DigitalNotificationPreference> userPreference = Optional.of(createNotificationMockUserPreference());
        List<NotificationPreferenceResponse> defaultPreferences = createNotificationMockDefaultPreferences();

        // Mock repository to return user preferences
        when(bankingNotificationPreferenceRepository.findByDigitalCustomerProfileId(digitalCustomerProfileId))
                .thenReturn(userPreference);
        when(configurationServiceClient.getDefaultNotificationPreferences()).thenReturn(defaultPreferences);

        // Call service method
        List<NotificationPreferenceResponse> actualPreferences =
                userRegistrationServiceImpl.getNotificationPreferences(digitalCustomerProfileId);

        // Verify method calls
        verify(bankingNotificationPreferenceRepository).findByDigitalCustomerProfileId(digitalCustomerProfileId);
        verify(configurationServiceClient).getDefaultNotificationPreferences();
        assertNotNull(actualPreferences);
        assertEquals(defaultPreferences.size(), actualPreferences.size()); // Check size of returned preferences
    }

    @Test
    void testGetNotificationPreferencesWithoutUserPreferences() throws Exception {
        // Prepare test data
        UUID digitalCustomerProfileId = UUID.randomUUID();
        Optional<DigitalNotificationPreference> userPreference = Optional.empty();
        List<NotificationPreferenceResponse> defaultPreferences = createNotificationMockDefaultPreferences();

        // Mock repository to return empty user preferences
        Mockito.when(bankingNotificationPreferenceRepository
                .findByDigitalCustomerProfileId(digitalCustomerProfileId)).thenReturn(userPreference);
        Mockito.when(configurationServiceClient.getDefaultNotificationPreferences())
                .thenReturn(defaultPreferences);

        List<NotificationPreferenceResponse> actualPreferences =
                userRegistrationServiceImpl.getNotificationPreferences(digitalCustomerProfileId);

        Mockito.verify(bankingNotificationPreferenceRepository)
                .findByDigitalCustomerProfileId(digitalCustomerProfileId);
        Mockito.verify(configurationServiceClient).getDefaultNotificationPreferences();
        assertNotNull(actualPreferences);
        assertEquals(defaultPreferences.size(), actualPreferences.size()); // Check size of returned preferences
    }

    @Test
    void testGetNotificationPreferencesWebClientResponseException() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        when(bankingNotificationPreferenceRepository.findByDigitalCustomerProfileId(digitalCustomerProfileId))
                .thenThrow(WebClientResponseException.class);

        assertThrows(WebClientResponseException.class, () -> {
            userRegistrationServiceImpl.getNotificationPreferences(digitalCustomerProfileId);
        });

        verify(bankingNotificationPreferenceRepository).findByDigitalCustomerProfileId(digitalCustomerProfileId);
        verify(configurationServiceClient, never()).getDefaultNotificationPreferences();
    }

    @Test
    void testGetNotificationPreferencesGenericException() throws Exception {
        UUID digitalCustomerProfileId = UUID.randomUUID();

        // Mock repository to throw generic Exception
        Mockito.when(bankingNotificationPreferenceRepository
                        .findByDigitalCustomerProfileId(digitalCustomerProfileId))
                .thenThrow(new RuntimeException("Some error"));

        assertThrows(NotificationPreferenceException.class, () -> {
            userRegistrationServiceImpl.getNotificationPreferences(digitalCustomerProfileId);
        });

        Mockito.verify(bankingNotificationPreferenceRepository)
                .findByDigitalCustomerProfileId(digitalCustomerProfileId);
        Mockito.verify(configurationServiceClient, never()).getDefaultNotificationPreferences();
    }

    private DigitalNotificationPreference createNotificationMockUserPreference() {
        return DigitalNotificationPreference.builder()
                .digitalBankingNotificationPreferenceId(1)
                .digitalCustomerProfileId(UUID.randomUUID())
                .emailNotificationBanking(true)
                .smsNotificationBanking(true)
                .mobilePushNotificationBanking(false)
                .notificationCreationDate(LocalDateTime.now())
                .notificationCreatedBy("test")
                .build();
    }

    private List<NotificationPreferenceResponse> createNotificationMockDefaultPreferences() {
        List<NotificationPreferenceResponse> defaultPreferences = new ArrayList<>();
        defaultPreferences.add(NotificationPreferenceResponse.builder()
                .notificationTypeElementName("hdrEmail")
                .notificationDescElementName("Email Notification")
                .notificationFlag(true)
                .build());
        defaultPreferences.add(NotificationPreferenceResponse.builder()
                .notificationTypeElementName("hdrSMS")
                .notificationDescElementName("SMS Notification")
                .notificationFlag(true)
                .build());
        defaultPreferences.add(NotificationPreferenceResponse.builder()
                .notificationTypeElementName("hdrMobilePush")
                .notificationDescElementName("PUSH Notification")
                .notificationFlag(true)
                .build());
        return defaultPreferences;
    }

    @Test
    void testSaveDeviceInfoMaxDevicesZero() {
        DigitalCustomerProfileDTO validDto;
        DigitalCustomerProfile validProfile;

        validDto = new DigitalCustomerProfileDTO();
        validDto.setDigitalCustomerProfileId(UUID.randomUUID().toString());
        validDto.setDigitalDeviceUdid(UUID.randomUUID().toString());
        validDto.setDevicePublicKeyForPin("publicKey");

        validProfile = new DigitalCustomerProfile();
        validProfile.setPinSetCompleted(true);

        DeviceRegistrationLimitResponse deviceRegistrationLimitResponse = new DeviceRegistrationLimitResponse();
        deviceRegistrationLimitResponse.setMaxRegisteredDevices(0);
        when(configurationServiceClient.getDeviceRegistrationMaxLimit()).thenReturn(deviceRegistrationLimitResponse);

        assertThrows(MaximumDevicesRegisteredException.class,
                () -> userRegistrationServiceImpl.saveDeviceInfo(validDto));
    }


    @Test
    void testSaveDeviceInfoPinNotSet() {
        DigitalCustomerProfileDTO validDto = createDigitalCustomerProfileDTORequest();
        DigitalCustomerProfile validProfile;

        validProfile = new DigitalCustomerProfile();
        validProfile.setPinSetCompleted(true);

        DeviceRegistrationLimitResponse deviceRegistrationLimitResponse = new DeviceRegistrationLimitResponse();
        deviceRegistrationLimitResponse.setMaxRegisteredDevices(maxDeviceLimit); // Set the limit as needed

        when(configurationServiceClient.getDeviceRegistrationMaxLimit()).thenReturn(deviceRegistrationLimitResponse);

        validProfile.setPinSetCompleted(false);
        when(digitalCustomerDeviceRepository
                .findRegisteredDevicesByDigitalCustomerProfileIdAndDeviceStatus(any(UUID.class)))
                .thenReturn(Collections.emptyList());
        when(digitalCustomerProfileRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(validProfile));

        assertThrows(PinNotExistException.class, () -> userRegistrationServiceImpl.saveDeviceInfo(validDto));
    }

    @Test
    void testSaveDeviceInfoProfileNotFound() {
        DigitalCustomerProfileDTO validDto = createDigitalCustomerProfileDTORequest();
        DeviceRegistrationLimitResponse deviceRegistrationLimitResponse = new DeviceRegistrationLimitResponse();
        deviceRegistrationLimitResponse.setMaxRegisteredDevices(maxDeviceLimit);

        when(configurationServiceClient.getDeviceRegistrationMaxLimit()).thenReturn(deviceRegistrationLimitResponse);

        when(digitalCustomerProfileRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(DigitalCustomerProfileIdNotFoundException.class,
                () -> userRegistrationServiceImpl.saveDeviceInfo(validDto));
    }

    @Test
    void testValidateInputThrowsExceptionOnEmptyId() {
        DigitalCustomerProfileDTO dtoWithEmptyId = new DigitalCustomerProfileDTO();
        assertThrows(IllegalArgumentException.class,
                () -> userRegistrationServiceImpl.saveDeviceInfo(dtoWithEmptyId));
    }

    @Test
    void testSaveDeviceInfoPinSetCompleted() {
        DigitalCustomerProfileDTO validDto = createDigitalCustomerProfileDTORequest();
        DigitalCustomerProfile validProfile = new DigitalCustomerProfile();
        validProfile.setPinSetCompleted(true);

        DeviceRegistrationLimitResponse deviceRegistrationLimitResponse = new DeviceRegistrationLimitResponse();
        deviceRegistrationLimitResponse.setMaxRegisteredDevices(MAX_DEVICES);

        DigitalCustomerDevice validDevice = new DigitalCustomerDevice();
        validDevice.setDigitalDeviceUdid(validDto.getDigitalDeviceUdid());

        DigitalCustomerProfile mockDigitalCustomerProfile = new DigitalCustomerProfile();
        mockDigitalCustomerProfile.setDigitalCustomerProfileId(UUID.fromString(validDto.getDigitalCustomerProfileId()));

        when(configurationServiceClient.getDeviceRegistrationMaxLimit()).thenReturn(deviceRegistrationLimitResponse);
        when(digitalCustomerProfileRepository.findById(any(UUID.class))).thenReturn(Optional.of(validProfile));
        when(digitalCustomerDeviceRepository.findRegisteredDevicesByDigitalCustomerProfileIdAndDeviceStatus(
                any(UUID.class))).thenReturn(Collections.emptyList());

        when(digitalCustomerDeviceRepository.findByDigitalCustomerProfileIdAndDigitalDeviceUdid(
                any(UUID.class), anyString())).thenReturn(validDevice);

        when(userInfoRepository.findByDigitalCustomerProfileId(any(UUID.class)))
                .thenReturn(Optional.of(mockDigitalCustomerProfile));

        doNothing().when(notificationUtil).sendNotification(any(), anyMap());

        UserAPIBaseResponse response = userRegistrationServiceImpl.saveDeviceInfo(validDto);

        assertNotNull(response);
        verify(notificationUtil).sendNotification(any(), anyMap());
    }
}
