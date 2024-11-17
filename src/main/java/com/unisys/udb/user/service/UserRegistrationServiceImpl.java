package com.unisys.udb.user.service;


import com.unisys.udb.user.constants.MarketingTypeEnum;
import com.unisys.udb.user.constants.MessageCodesConstants;
import com.unisys.udb.user.constants.NotificationTypeEnum;
import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.BankingNotificationPreferenceRequest;
import com.unisys.udb.user.dto.request.DigitalCustomerProfileDTO;
import com.unisys.udb.user.dto.request.MarketingNotificationPreferenceRequest;
import com.unisys.udb.user.dto.request.PublicKeyUpdateRequest;
import com.unisys.udb.user.dto.request.UserDetailDto;
import com.unisys.udb.user.dto.request.UserPublicKeyRequest;
import com.unisys.udb.user.dto.response.BankingNotificationPreferenceResponse;
import com.unisys.udb.user.dto.response.BiometricPublicKeyResponse;
import com.unisys.udb.user.dto.response.CheckMfaStatusResponse;
import com.unisys.udb.user.dto.response.CheckPinStatusResponse;
import com.unisys.udb.user.dto.response.CoreCustomerProfileResponse;
import com.unisys.udb.user.dto.response.DigitalCookiePreferenceResponse;
import com.unisys.udb.user.dto.response.MarketingNotificationPreferenceResponse;
import com.unisys.udb.user.dto.response.MarketingPreferenceResponse;
import com.unisys.udb.user.dto.response.NotificationPreferenceResponse;
import com.unisys.udb.user.dto.response.UpdatePinStatusResponse;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.dto.response.UserInfoResponse;
import com.unisys.udb.user.dto.response.UserNameResponse;
import com.unisys.udb.user.entity.DigitalCustomerDevice;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import com.unisys.udb.user.entity.DigitalDeviceLink;
import com.unisys.udb.user.entity.DigitalMarketingNotificationPreference;
import com.unisys.udb.user.entity.DigitalNotificationPreference;
import com.unisys.udb.user.exception.CoreCustomerProfileAlreadyExistsException;
import com.unisys.udb.user.exception.CoreCustomerProfileEmptyException;
import com.unisys.udb.user.exception.CoreCustomerProfileIdNotFoundException;
import com.unisys.udb.user.exception.DatabaseOperationsException;
import com.unisys.udb.user.exception.DigitalCustomerDeviceNotFoundException;
import com.unisys.udb.user.exception.DigitalCustomerProfileIdNotFoundException;
import com.unisys.udb.user.exception.DigitalCustomerProfileIdNotNullException;
import com.unisys.udb.user.exception.DigitalDeviceUdidNotFoundException;
import com.unisys.udb.user.exception.DuplicationKeyException;
import com.unisys.udb.user.exception.InvalidArgumentException;
import com.unisys.udb.user.exception.InvalidDigitalDeviceUdid;
import com.unisys.udb.user.exception.MarketingPreferenceException;
import com.unisys.udb.user.exception.MaximumDevicesRegisteredException;
import com.unisys.udb.user.exception.NotificationPreferenceException;
import com.unisys.udb.user.exception.PinNotExistException;
import com.unisys.udb.user.exception.RegistrationNotificationNotPublished;
import com.unisys.udb.user.exception.UserDeviceNotLinkedException;
import com.unisys.udb.user.exception.UserNameNotFoundException;
import com.unisys.udb.user.exception.UserPublicKeyAlreadyExistException;
import com.unisys.udb.user.exception.UserPublicKeyNotFoundException;
import com.unisys.udb.user.repository.BankingNotificationPreferenceRepository;
import com.unisys.udb.user.repository.DigitalCookiePreferenceRepository;
import com.unisys.udb.user.repository.DigitalCustomerDeviceRepository;
import com.unisys.udb.user.repository.DigitalCustomerProfileRepository;
import com.unisys.udb.user.repository.DigitalDeviceLinkRepository;
import com.unisys.udb.user.repository.MarketingNotificationPreferencesRepository;
import com.unisys.udb.user.repository.UserInfoRepository;
import com.unisys.udb.user.service.client.ConfigurationServiceClient;
import com.unisys.udb.user.utils.dto.response.CommonUtil;
import com.unisys.udb.user.utils.dto.response.NotificationUtil;
import com.unisys.udb.user.utils.masking.LogMasking;
import com.unisys.udb.utility.linkmessages.dto.DynamicMessageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.unisys.udb.user.constants.MessageCodesConstants.CORE_CUSTOMER_PROFILE_ALREADY_EXIST_ERROR_CODE;
import static com.unisys.udb.user.constants.MessageCodesConstants.CORE_CUSTOMER_PROFILE_MISSING_ERROR_CODE;
import static com.unisys.udb.user.constants.MessageCodesConstants.DUPLICATION_KEY_VIOLATION_ERROR_CODE;
import static com.unisys.udb.user.constants.UdbConstants.CORE_CUSTOMER_PROFILE_ALREADY_EXISTS;
import static com.unisys.udb.user.constants.UdbConstants.CORE_CUSTOMER_PROFILE_ID_MISSING;
import static com.unisys.udb.user.constants.UdbConstants.DEVICE_REGISTRATION_SUCCESS;
import static com.unisys.udb.user.constants.UdbConstants.DUPLICATE_KEY_VIOLATION;
import static com.unisys.udb.user.constants.UdbConstants.ERROR_RETRIEVING_PUBLIC_KEY_FOR_DEVICE_UDID;
import static com.unisys.udb.user.constants.UdbConstants.EXCEPTION;
import static com.unisys.udb.user.constants.UdbConstants.FACE_ID;
import static com.unisys.udb.user.constants.UdbConstants.FAILURE;
import static com.unisys.udb.user.constants.UdbConstants.FOUR_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.LANGUAGE_PREFERENCE;
import static com.unisys.udb.user.constants.UdbConstants.MAX_LENGTH_FIFTY;
import static com.unisys.udb.user.constants.UdbConstants.NOT_FOUND_ERROR_CODE;
import static com.unisys.udb.user.constants.UdbConstants.NOT_FOUND_ERROR_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.ONE_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.PIN_NOT_SETUP_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.PUBLIC_KEY_NOT_FOUND_FOR_THE_GIVEN_DEVICE_UUID;
import static com.unisys.udb.user.constants.UdbConstants.STATUS_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.SUCCESS;
import static com.unisys.udb.user.constants.UdbConstants.SUCCESS_CODE;
import static com.unisys.udb.user.constants.UdbConstants.THREE_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.TOUCH_ID;
import static com.unisys.udb.user.constants.UdbConstants.TWO_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.UDB_BANKING_PRFNC_HDR_EMAIL;
import static com.unisys.udb.user.constants.UdbConstants.UDB_BANKING_PRFNC_HDR_MOBILE_PUSH;
import static com.unisys.udb.user.constants.UdbConstants.UDB_BANKING_PRFNC_HDR_SMS;
import static com.unisys.udb.user.constants.UdbConstants.UDB_MARKETING_PRFNC_HDR_EMAIL;
import static com.unisys.udb.user.constants.UdbConstants.UDB_MARKETING_PRFNC_HDR_ONLINE;
import static com.unisys.udb.user.constants.UdbConstants.UDB_MARKETING_PRFNC_HDR_POST;
import static com.unisys.udb.user.constants.UdbConstants.UDB_MARKETING_PRFNC_HDR_SMS;
import static com.unisys.udb.user.constants.UdbConstants.UDB_MARKETING_PRFNC_HDR_TELEPHONE;
import static com.unisys.udb.user.constants.UdbConstants.USER_AND_DEVICE_INFO_SAVED;
import static com.unisys.udb.user.constants.UdbConstants.USER_SERVICE;
import static com.unisys.udb.user.constants.UdbConstants.ZERO_CONSTANT;
import static org.apache.logging.log4j.util.Strings.isEmpty;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final UserInfoRepository userInfoRepository;
    private final DigitalCustomerProfileRepository digitalCustomerProfileRepository;
    private final BankingNotificationPreferenceRepository bankingNotificationPreferenceRepository;
    private final MarketingNotificationPreferencesRepository marketingNotificationPreferencesRepository;
    private final DigitalCustomerProfileAndDeviceInjector digitalCustomerProfileAndDeviceInjector;
    private final DigitalCookiePreferenceRepository digitalCookiePreferenceRepository;

    private final DigitalCustomerDeviceRepository digitalCustomerDeviceRepository;
    private final DigitalDeviceLinkRepository digitalDeviceLinkRepository;
    private final ConfigurationServiceClient configurationServiceClient;

    private final NotificationUtil notificationUtil;

    public static Date convertToDate(final LocalDate localDate) {
        return Date.valueOf(localDate);
    }

    @Override
    public Mono<UserInfoResponse> getUserInfo(final UUID digitalCustomerProfileId, final String digitalDeviceUdid) {

        log.info("Inside getUserInfo() method for the digitalCustomerProfileId: {}", digitalCustomerProfileId);
        Optional<DigitalCustomerProfile> existingCustomer = userInfoRepository
                .findByDigitalCustomerProfileId(digitalCustomerProfileId);
        if (Boolean.FALSE.equals(userInfoRepository.existsByDigitalCustomerProfileId(digitalCustomerProfileId))) {
            List<String> errorCode = new ArrayList<>();
            errorCode.add(NOT_FOUND_ERROR_CODE);
            List<String> params = new ArrayList<>();
            params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT)));
            throw new DigitalCustomerProfileIdNotFoundException(errorCode,
                    HttpStatus.NOT_FOUND,
                    FAILURE,
                    NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId,
                    params);
        }

        UserInfoResponse fetchUserInfoResponse = new UserInfoResponse();
        fetchUserInfoResponse.setDigitalCustomerProfileId(digitalCustomerProfileId);
        fetchUserInfoResponse.setDigitalUserName(existingCustomer.get().getDigitalUserName());
        fetchUserInfoResponse.setCoreCustomerProfileId(existingCustomer.get().getCoreCustomerProfileId());
        fetchUserInfoResponse.setDigitalCustomerStatusTypeId(existingCustomer.get().getDigitalCustomerStatusTypeId());
        List<String> digitalCustomerDeviceIds = digitalCustomerProfileRepository
                .findDigitalCustomerDeviceIdList(digitalCustomerProfileId);

        if (digitalCustomerDeviceIds == null || digitalCustomerDeviceIds.isEmpty()) {
            log.error("No devices found for the digitalCustomerProfileId: {}", digitalCustomerProfileId);
            throw new UserDeviceNotLinkedException("No devices found for the profile Id");
        }

        if (digitalCustomerDeviceIds.stream()
                .anyMatch(deviceUdId -> deviceUdId.equals(digitalDeviceUdid))) {
            fetchUserInfoResponse.setDigitalCustomerDeviceId(digitalDeviceUdid);
        } else if (digitalDeviceUdid != null && !digitalCustomerDeviceIds.contains(digitalDeviceUdid)) {
            log.error("Device with UDID {} is not linked with the profile Id {}",
                    digitalDeviceUdid, digitalCustomerProfileId);
            throw new UserDeviceNotLinkedException("Device is not linked with profile Id");
        }

        return Mono.just(fetchUserInfoResponse);
    }


    @Override
    public Mono<List<MarketingPreferenceResponse>> updateBankingNotificationPreference(
            final UUID digitalCustomerProfileId,
            final BankingNotificationPreferenceRequest request) {

        log.info("Inside CREATE or UPDATE banking notification preference for the digital_customer_profile_id: {}",
                digitalCustomerProfileId);

        //check for the valid request
        boolean allFalse = request.getNotificationPreferenceList().values()
                .stream().noneMatch(value -> value);
        if (allFalse) {
            throw new InvalidArgumentException("At least one communication channel should be enabled");
        }

        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        List<String> params = new ArrayList<>();
        params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT)));

        if (digitalCustomerProfileRepository.findById(digitalCustomerProfileId).isEmpty()) {
            throw new DigitalCustomerProfileIdNotFoundException(
                    errorCode, HttpStatus.NOT_FOUND, FAILURE, NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId,
                    params);
        }

        final String userNameByDigitalCustomerProfileId = userInfoRepository
                .findUserNameByDigitalCustomerProfileId(digitalCustomerProfileId);

        Optional<DigitalNotificationPreference> bankingNotificationPreferenceOpt =
                bankingNotificationPreferenceRepository
                        .findByDigitalCustomerProfileId(digitalCustomerProfileId);

        if (bankingNotificationPreferenceOpt.isPresent()) {
            DigitalNotificationPreference bankingNotificationPreference = bankingNotificationPreferenceOpt.get();
            mapToBankingNotificationPreference(request, bankingNotificationPreference);
            bankingNotificationPreference.setNotificationModificationDate(LocalDateTime.now());
            bankingNotificationPreference.setNotificationModifiedBy(userNameByDigitalCustomerProfileId);
            bankingNotificationPreferenceRepository.saveAndFlush(bankingNotificationPreference);
            log.info("Banking notification preference updated successfully: {} ", digitalCustomerProfileId);
        } else {
            DigitalNotificationPreference bankingNotificationPreference = new DigitalNotificationPreference();
            mapToBankingNotificationPreference(request, bankingNotificationPreference);
            bankingNotificationPreference.setDigitalCustomerProfileId(digitalCustomerProfileId);
            bankingNotificationPreference.setNotificationCreatedBy(userNameByDigitalCustomerProfileId);
            bankingNotificationPreference.setNotificationCreationDate(LocalDateTime.now());
            bankingNotificationPreferenceRepository.saveAndFlush(bankingNotificationPreference);
            log.info("Banking notification preference created successfully: {} ", digitalCustomerProfileId);
        }

        // Fetch and return marketing preferences after update/create
        return Mono.just(this.getMarketingPreferences(digitalCustomerProfileId));
    }

    @Override
    public Mono<DynamicMessageResponse> updateMarketingNotificationPreference(
            final UUID digitalCustomerProfileId,
            final MarketingNotificationPreferenceRequest request) {

        log.debug("Inside CREATE or UPDATE marketing notification preference for the digital_customer_profile_id: {}",
                digitalCustomerProfileId);

        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        List<String> params = new ArrayList<>();
        params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT)));

        if (digitalCustomerProfileRepository.findById(digitalCustomerProfileId).isEmpty()) {
            throw new DigitalCustomerProfileIdNotFoundException(
                    errorCode, HttpStatus.NOT_FOUND, FAILURE, NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId,
                    params);
        }

        final String userNameByDigitalCustomerProfileId = userInfoRepository
                .findUserNameByDigitalCustomerProfileId(digitalCustomerProfileId);

        Optional<DigitalMarketingNotificationPreference> marketingNotificationPreferenceOpt =
                marketingNotificationPreferencesRepository
                        .findByDigitalCustomerProfileId(digitalCustomerProfileId);

        if (marketingNotificationPreferenceOpt.isPresent()) {
            DigitalMarketingNotificationPreference marketingNotificationPreference =
                    marketingNotificationPreferenceOpt.get();
            if (marketingNotificationPreference != null) {

                mapToDigitalMarketingNotificationPreference(request, marketingNotificationPreference);

                marketingNotificationPreference.setNotificationModificationDate(LocalDateTime.now());
                marketingNotificationPreference.setNotificationModifiedBy(userNameByDigitalCustomerProfileId);
                marketingNotificationPreferencesRepository.saveAndFlush(marketingNotificationPreference);
                log.info("Marketing notification preference updated successfully: {} ", digitalCustomerProfileId);
            }
        } else {
            DigitalMarketingNotificationPreference marketingNotificationPreference =
                    new DigitalMarketingNotificationPreference();

            mapToDigitalMarketingNotificationPreference(request, marketingNotificationPreference);

            marketingNotificationPreference.setDigitalCustomerProfileId(digitalCustomerProfileId);
            marketingNotificationPreference.setNotificationCreatedBy(userNameByDigitalCustomerProfileId);
            marketingNotificationPreference.setNotificationCreationDate(LocalDateTime.now());

            marketingNotificationPreferencesRepository.saveAndFlush(marketingNotificationPreference);
            log.info("Marketing notification preference created successfully: {} ", digitalCustomerProfileId);
        }

        DynamicMessageResponse response = new DynamicMessageResponse(
                SUCCESS,
                "Marketing notification preference created or updated successfully.",
                List.of(new DynamicMessageResponse.Message(
                        MessageCodesConstants.MARKETING_PRFNC_CREATE_UPDATE_SUCCESS_CODE,
                        Collections.emptyList())));
        return Mono.just(response);
    }

    private static void mapToDigitalMarketingNotificationPreference(MarketingNotificationPreferenceRequest request,
                                                                    DigitalMarketingNotificationPreference
                                                                            marketingNotificationPreference) {

        if (request.getMarketingPreferenceList().containsKey(UDB_MARKETING_PRFNC_HDR_EMAIL)) {
            marketingNotificationPreference.setMarketingEmailNotification(
                    request.getMarketingPreferenceList().get(UDB_MARKETING_PRFNC_HDR_EMAIL));
        }
        if (request.getMarketingPreferenceList().containsKey(UDB_MARKETING_PRFNC_HDR_SMS)) {
            marketingNotificationPreference.setMarketingSmsNotification(
                    request.getMarketingPreferenceList().get(UDB_MARKETING_PRFNC_HDR_SMS));
        }
        if (request.getMarketingPreferenceList().containsKey(UDB_MARKETING_PRFNC_HDR_POST)) {
            marketingNotificationPreference.setMarketingPostNotification(
                    request.getMarketingPreferenceList().get(UDB_MARKETING_PRFNC_HDR_POST));
        }

        if (request.getMarketingPreferenceList().containsKey(UDB_MARKETING_PRFNC_HDR_TELEPHONE)) {
            marketingNotificationPreference.setMarketingTelephoneNotification(
                    request.getMarketingPreferenceList().get(UDB_MARKETING_PRFNC_HDR_TELEPHONE));
        }
        if (request.getMarketingPreferenceList().containsKey(UDB_MARKETING_PRFNC_HDR_ONLINE)) {
            marketingNotificationPreference.setMarketingOnlineNotification(
                    request.getMarketingPreferenceList().get(UDB_MARKETING_PRFNC_HDR_ONLINE));
        }
    }

    private static void mapToBankingNotificationPreference(BankingNotificationPreferenceRequest request,
                                                                    DigitalNotificationPreference
                                                                            notificationPreference) {
        if (request.getNotificationPreferenceList().containsKey(UDB_BANKING_PRFNC_HDR_EMAIL)) {
            notificationPreference.setEmailNotificationBanking(
                    request.getNotificationPreferenceList().get(UDB_BANKING_PRFNC_HDR_EMAIL));
        }
        if (request.getNotificationPreferenceList().containsKey(UDB_BANKING_PRFNC_HDR_SMS)) {
            notificationPreference.setSmsNotificationBanking(
                    request.getNotificationPreferenceList().get(UDB_BANKING_PRFNC_HDR_SMS));
        }
        if (request.getNotificationPreferenceList().containsKey(UDB_BANKING_PRFNC_HDR_MOBILE_PUSH)) {
            notificationPreference.setMobilePushNotificationBanking(
                    request.getNotificationPreferenceList().get(UDB_BANKING_PRFNC_HDR_MOBILE_PUSH));
        }
    }

    @Override
    public Mono<BankingNotificationPreferenceResponse> getBankingPreference(final UUID digitalCustomerProfileId) {
        log.info("Inside getBankingPreference() for the digital_customer_profile_id: {}", digitalCustomerProfileId);
        return Mono.justOrEmpty(bankingNotificationPreferenceRepository.findByDigitalCustomerProfileId(
                digitalCustomerProfileId))
                .map(bankingNotificationPreferenceById -> {
                    BankingNotificationPreferenceResponse bankingPreference =
                            new BankingNotificationPreferenceResponse();
                    bankingPreference.setMobilePushNotificationBanking(
                            bankingNotificationPreferenceById.isMobilePushNotificationBanking());
                    bankingPreference.setEmailNotificationBanking(
                            bankingNotificationPreferenceById.isEmailNotificationBanking());
                    bankingPreference.setSmsNotificationBanking(
                            bankingNotificationPreferenceById.isSmsNotificationBanking());
                    bankingPreference.setCreatedDate(bankingNotificationPreferenceById.getNotificationCreationDate());
                    bankingPreference.setCreatedBy(bankingNotificationPreferenceById.getNotificationCreatedBy());
                    bankingPreference.setUpdatedDate(bankingNotificationPreferenceById
                            .getNotificationModificationDate());
                    bankingPreference.setUpdatedBy(bankingNotificationPreferenceById.getNotificationModifiedBy());
                    return bankingPreference;
                })
                .switchIfEmpty(Mono.defer(() -> Mono.error(new DigitalCustomerProfileIdNotFoundException(
                        Collections.singletonList(NOT_FOUND_ERROR_CODE),
                        HttpStatus.NOT_FOUND,
                        FAILURE,
                        NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId,
                        Collections.singletonList(LocalDateTime.now().format(DateTimeFormatter
                                .ofPattern(UdbConstants.DATE_FORMAT)))
                ))));
    }



    @Override
    public Mono<MarketingNotificationPreferenceResponse> getMarketingPreference(final UUID digitalCustomerProfileId) {
        log.info("Inside getMarketingPreference() for the digital_customer_profile_id: {}", digitalCustomerProfileId);
        return Mono.justOrEmpty(marketingNotificationPreferencesRepository
                        .findByDigitalCustomerProfileId(digitalCustomerProfileId))
                .map(marketingNotificationPreferenceById -> {
                    MarketingNotificationPreferenceResponse marketingPreference =
                            new MarketingNotificationPreferenceResponse();
                    marketingPreference.setEmailNotificationBanking(
                            marketingNotificationPreferenceById.isMarketingEmailNotification());
                    marketingPreference.setSmsNotificationBanking(
                            marketingNotificationPreferenceById.isMarketingSmsNotification());
                    marketingPreference.setPostNotificationMarketing(
                            marketingNotificationPreferenceById.isMarketingPostNotification());
                    marketingPreference.setTelephoneNotificationMarketing(
                            marketingNotificationPreferenceById.isMarketingTelephoneNotification());
                    marketingPreference.setOnlineNotificationMarketing(
                            marketingNotificationPreferenceById.isMarketingOnlineNotification());
                    marketingPreference.setCreatedDate(marketingNotificationPreferenceById
                            .getNotificationCreationDate());
                    marketingPreference.setCreatedBy(marketingNotificationPreferenceById.getNotificationCreatedBy());
                    marketingPreference.setUpdatedDate(marketingNotificationPreferenceById
                            .getNotificationModificationDate());
                    marketingPreference.setUpdatedBy(marketingNotificationPreferenceById.getNotificationModifiedBy());
                    return marketingPreference;
                })
                .switchIfEmpty(Mono.defer(() -> Mono.error(new DigitalCustomerProfileIdNotFoundException(
                        Collections.singletonList(NOT_FOUND_ERROR_CODE),
                        HttpStatus.NOT_FOUND,
                        FAILURE,
                        NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId,
                        Collections.singletonList(LocalDateTime.now().format(DateTimeFormatter
                                .ofPattern(UdbConstants.DATE_FORMAT)))
                ))));
    }


    @Override
    @Transactional
    public Mono<DynamicMessageResponse> saveUserAndDeviceInfo(
            final DigitalCustomerProfileDTO digitalCustomerProfileDTO) {

        // Check and validate input strings
        if (isEmpty(digitalCustomerProfileDTO.getCoreCustomerProfileId())) {
            log.debug("Core Customer id is missing in the request");
            List<String> errorCode = new ArrayList<>();
            errorCode.add(CORE_CUSTOMER_PROFILE_MISSING_ERROR_CODE);
            throw new CoreCustomerProfileEmptyException(errorCode,
                    HttpStatus.BAD_REQUEST,
                    FAILURE,
                    CORE_CUSTOMER_PROFILE_ID_MISSING,
                    new ArrayList<>());
        }
        // Check if the coreCustomerProfileId already exists
        UUID coreCustomerProfileId = UUID.fromString(digitalCustomerProfileDTO.getCoreCustomerProfileId());
        if (Boolean.TRUE.equals(digitalCustomerProfileRepository.existsByCoreCustomerProfileId(
                coreCustomerProfileId))) {
            log.debug("Core customer profile with ID {} already exists.", coreCustomerProfileId);

            List<String> errorCode = new ArrayList<>();
            errorCode.add(CORE_CUSTOMER_PROFILE_ALREADY_EXIST_ERROR_CODE);
            throw new CoreCustomerProfileAlreadyExistsException(errorCode,
                    HttpStatus.CONFLICT,
                    FAILURE,
                    CORE_CUSTOMER_PROFILE_ALREADY_EXISTS,
                    new ArrayList<>());

        }
        if (Boolean.TRUE.equals(digitalCustomerProfileRepository.existsById(
                UUID.fromString(digitalCustomerProfileDTO.getDigitalCustomerProfileId())))) {
            log.debug("Duplicate key violation: ");
            List<String> errorCode = new ArrayList<>();
            errorCode.add(DUPLICATION_KEY_VIOLATION_ERROR_CODE);
            throw new DuplicationKeyException(errorCode,
                    HttpStatus.CONFLICT,
                    FAILURE,
                    DUPLICATE_KEY_VIOLATION,
                    new ArrayList<>());
        }

        try {
            log.info("UserService- Staring to insert user and device information");
            digitalCustomerProfileAndDeviceInjector.insertDigitalProfileDeviceLink(digitalCustomerProfileDTO);
            DynamicMessageResponse.Message message = new DynamicMessageResponse.Message(
                    MessageCodesConstants.REGISTRATION_SUCCESS_CODE, new ArrayList<>());
            List<DynamicMessageResponse.Message> messages = new ArrayList<>();
            List<String> errorCodes = new ArrayList<>();
            messages.add(message);
            DynamicMessageResponse dynamicMessageResponse = new DynamicMessageResponse(
                    SUCCESS, USER_AND_DEVICE_INFO_SAVED, messages
            );

            sendNotification(digitalCustomerProfileDTO, errorCodes,
                    "triggerSuccessfulRegistrationNotification", "SuccessfulRegistration Template");

            return Mono.just(dynamicMessageResponse);
        } catch (Exception e) {
            log.error("insertDigitalProfileDeviceLink" + EXCEPTION, ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    private void sendNotification(DigitalCustomerProfileDTO digitalCustomerProfileDTO, List<String> errorCodes,
            String activity, String template) {
        try {
            notificationUtil.sendNotification(notificationUtil.prepareRequiredFieldsMap(
                    UUID.fromString(digitalCustomerProfileDTO.getDigitalCustomerProfileId()),
                    digitalCustomerProfileDTO.getDigitalUserName(), USER_SERVICE,
                    activity, template,
                    LANGUAGE_PREFERENCE), new HashMap<>());
        } catch (Exception e) {
            errorCodes.add("errorPublishNotificationFailureException");
            throw new RegistrationNotificationNotPublished(errorCodes, HttpStatus.SERVICE_UNAVAILABLE, FAILURE,
                    "Registration is not published",
                    Collections.emptyList());
        }
    }


    @Override
    @Transactional
    public UserAPIBaseResponse saveDeviceInfo(final DigitalCustomerProfileDTO digitalCustomerProfileDTO) {
        log.debug("Inside saveDeviceInfo()");
        validateInput(digitalCustomerProfileDTO);

        int maxDeviceLimit = configurationServiceClient.getDeviceRegistrationMaxLimit().getMaxRegisteredDevices();

        List<Object[]> registeredDevices = findRegisteredDevices(digitalCustomerProfileDTO);

        if (maxDeviceLimit == ZERO_CONSTANT) {
            throw new MaximumDevicesRegisteredException("Maximum device registration limit cannot be zero");
        }

        if (registeredDevices.size() >= maxDeviceLimit) {
            throw new MaximumDevicesRegisteredException("More than " + maxDeviceLimit + " devices can't be registered");
        }

        DigitalCustomerProfile digitalCustomerProfile = findDigitalCustomerProfile(digitalCustomerProfileDTO);
        if (digitalCustomerProfile.isPinSetCompleted()) {
            List<String> errorCodes = new ArrayList<>();
            handleDeviceRegistration(digitalCustomerProfileDTO, digitalCustomerProfile);
            sendNotification(digitalCustomerProfileDTO, errorCodes, "triggerSuccessfulRegistrationNotification",
                    "SuccessfulRegistration Template");
            return buildResponse(HttpStatus.OK, STATUS_MESSAGE, DEVICE_REGISTRATION_SUCCESS);
        } else {
            throw new PinNotExistException(PIN_NOT_SETUP_MESSAGE);
        }
    }

    private void validateInput(DigitalCustomerProfileDTO digitalCustomerProfileDTO) {
        if (isEmpty(digitalCustomerProfileDTO.getDigitalCustomerProfileId())) {
            throw new IllegalArgumentException("digital customer profile ID cannot be empty or null");
        }
    }

    private List<Object[]> findRegisteredDevices(DigitalCustomerProfileDTO digitalCustomerProfileDTO) {
        return digitalCustomerDeviceRepository.findRegisteredDevicesByDigitalCustomerProfileIdAndDeviceStatus(
                UUID.fromString(digitalCustomerProfileDTO.getDigitalCustomerProfileId()));
    }

    private DigitalCustomerProfile findDigitalCustomerProfile(DigitalCustomerProfileDTO digitalCustomerProfileDTO) {
        List<String> errorCodes = new ArrayList<>();
        errorCodes.add("404");
        List<String> params = new ArrayList<>();
        params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT)));
        Optional<DigitalCustomerProfile> profile = digitalCustomerProfileRepository
                .findById(UUID.fromString(digitalCustomerProfileDTO.getDigitalCustomerProfileId()));
        if (profile.isPresent()) {
            return profile.get();

        } else {
            throw new DigitalCustomerProfileIdNotFoundException(errorCodes, HttpStatus.NOT_FOUND,
                    "Digital Customer Profile ID not Found", FAILURE, params);
        }
    }

    private Optional<DigitalCustomerDevice> findDigitalCustomerDevice(
            DigitalCustomerProfileDTO digitalCustomerProfileDTO) {
        return digitalCustomerDeviceRepository.getDigitalCustomerDeviceByDeviceUUIDAndProfileID(
                digitalCustomerProfileDTO.getDigitalCustomerProfileId(),
                digitalCustomerProfileDTO.getDigitalDeviceUdid());
    }

    private void handleDeviceRegistration(
            DigitalCustomerProfileDTO digitalCustomerProfileDTO, DigitalCustomerProfile digitalCustomerProfile) {
        Optional<DigitalCustomerDevice> digitalCustomerDevice = findDigitalCustomerDevice(digitalCustomerProfileDTO);

        if (digitalCustomerDevice.isEmpty()) {
            registerNewDevice(digitalCustomerProfileDTO, digitalCustomerProfile);

            PublicKeyUpdateRequest publicKeyUpdateRequest = new PublicKeyUpdateRequest();
            publicKeyUpdateRequest.setDeviceUdid(digitalCustomerProfileDTO.getDigitalDeviceUdid());
            publicKeyUpdateRequest.setDigitalCustomerProfile(
                    UUID.fromString(digitalCustomerProfileDTO.getDigitalCustomerProfileId()));
            publicKeyUpdateRequest.setDevicePublicKey(digitalCustomerProfileDTO.getDevicePublicKeyForPin());
            saveUserPublicKeyForPin(publicKeyUpdateRequest);
        } else {
            updateExistingDevice(digitalCustomerProfileDTO, digitalCustomerProfile, digitalCustomerDevice.get());
        }
    }

    private void registerNewDevice(DigitalCustomerProfileDTO digitalCustomerProfileDTO,
                                   DigitalCustomerProfile digitalCustomerProfile) {
        try {
            digitalCustomerProfileAndDeviceInjector
                    .getCustomerDevice(digitalCustomerProfileDTO, digitalCustomerProfile);
        } catch (Exception e) {
            throw new DatabaseOperationsException("SQL error");
        }
    }

    private void updateExistingDevice(DigitalCustomerProfileDTO digitalCustomerProfileDTO,
                                      DigitalCustomerProfile digitalCustomerProfile,
                                      DigitalCustomerDevice digitalCustomerDevice) {
        int count = registerDevice(digitalCustomerProfileDTO);

        if (count == 0) {
            throw new DatabaseOperationsException("Unable to update flag");
        }

        DigitalDeviceLink digitalDeviceLink = findDigitalDeviceLink(digitalCustomerProfileDTO, digitalCustomerProfile,
                digitalCustomerDevice);
        try {
            digitalCustomerProfileAndDeviceInjector.saveDeviceAudit(digitalCustomerProfileDTO, digitalCustomerProfile,
                    digitalCustomerDevice, digitalDeviceLink);
        } catch (Exception e) {
            throw new DatabaseOperationsException("SQL error");
        }

    }

    private DigitalDeviceLink findDigitalDeviceLink(DigitalCustomerProfileDTO digitalCustomerProfileDTO,
                                                    DigitalCustomerProfile digitalCustomerProfile,
                                                    DigitalCustomerDevice digitalCustomerDevice) {
        int count = digitalDeviceLinkRepository.registerDevices(
                UUID.fromString(digitalCustomerProfileDTO.getDigitalCustomerProfileId()),
                digitalCustomerProfileDTO.getDigitalDeviceUdid());
        if (count == 0) {
            throw new DatabaseOperationsException("Unable to update flag");
        }
        Integer customerDeviceId = digitalCustomerDeviceRepository.
                findDeviceLinkByCustomerProfileIdAndDeviceUUID(
                        UUID.fromString(digitalCustomerProfileDTO.getDigitalCustomerProfileId()),
                        digitalCustomerProfileDTO.getDigitalDeviceUdid());

        DigitalDeviceLink digitalDeviceLink = digitalDeviceLinkRepository.findByDigitalCustomerDeviceID(
                customerDeviceId);

        digitalCustomerProfileAndDeviceInjector.saveDeviceAudit(digitalCustomerProfileDTO,
                digitalCustomerProfile, digitalCustomerDevice, digitalDeviceLink);
        return digitalDeviceLink;

    }

    private int registerDevice(DigitalCustomerProfileDTO digitalCustomerProfileDTO) {
        return digitalDeviceLinkRepository.registerDevices(
                UUID.fromString(digitalCustomerProfileDTO.getDigitalCustomerProfileId()),
                digitalCustomerProfileDTO.getDigitalDeviceUdid());
    }

    private UserAPIBaseResponse buildResponse(HttpStatus httpStatus, String statusMessage, String message) {
        return UserAPIBaseResponse.builder()
                .httpStatus(httpStatus)
                .status(statusMessage)
                .message(message)
                .timeStamp(CommonUtil.getCurrentDateInUTC())
                .build();
    }


    @Override
    public Mono<CoreCustomerProfileResponse> fetchUserInfo(final UUID digitalCustomerProfileId) {
        final String maskedDigitalProfileId = LogMasking.maskingDigitlProfileId(digitalCustomerProfileId);
        log.info("UserService- Inside UserService fetchUserInfo " + maskedDigitalProfileId);
        if (Boolean.FALSE.equals(userInfoRepository.existsByDigitalCustomerProfileId(digitalCustomerProfileId))) {
            List<String> errorCode = new ArrayList<>();
            errorCode.add(NOT_FOUND_ERROR_CODE);
            List<String> params = new ArrayList<>();
            params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT)));
            throw new DigitalCustomerProfileIdNotFoundException(errorCode,
                    HttpStatus.NOT_FOUND,
                    FAILURE,
                    NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId,
                    params);
        }
        List<Object[]> coreCustomerProfile = userInfoRepository.fetchCustomerByDigitalCustomerProfileId(
                digitalCustomerProfileId);
        if (coreCustomerProfile.isEmpty()) {
            log.info(" Entered fetchUserInfo coreCustomerProfile empty ");
            throw new CoreCustomerProfileIdNotFoundException(digitalCustomerProfileId);
        } else {
            log.info(" Entered fetchUserInfo coreCustomerProfile else ");
            return Mono.just(mapToCoreCustomerProfileResponse(coreCustomerProfile.get(0)));
        }
    }

    @Override
    public Mono<UserNameResponse> getUserNameInfoByCustomerDeviceId(final Integer digitalCustomerDeviceId) {
        log.info("Inside getUserNameInfoByCustomerDeviceId with digitalCustomerDeviceId: {}", digitalCustomerDeviceId);
        return Optional.ofNullable(userInfoRepository.getUserNameInfoByCustomerDeviceId(digitalCustomerDeviceId))
                .filter(result -> !result.isEmpty())
                .map(result -> {
                    String userName = (String) result.get(ZERO_CONSTANT)[ONE_CONSTANT];
                    log.info("Username information found for digitalCustomerDeviceId {}: {}",
                            digitalCustomerDeviceId, userName);
                    return Mono.just(UserNameResponse.builder()
                            .digitalCustomerDeviceId(digitalCustomerDeviceId)
                            .digitalUserName(userName)
                            .build());
                })
                .orElseThrow(() -> {
                    log.warn("No username information found for digitalCustomerDeviceId: {}", digitalCustomerDeviceId);
                    return new UserNameNotFoundException("No username information found for digitalCustomerDeviceId: "
                            + digitalCustomerDeviceId);
                });
    }


    @Override
    public Mono<DigitalCookiePreferenceResponse> getDigitalCookiePreference(final UUID digitalCustomerProfileId) {

        log.info("Inside getDigitalCookiePreference() for the digital_customer_profile_id: {}",
                digitalCustomerProfileId);

        return Optional.ofNullable(digitalCustomerProfileId)
                .filter(digitalCookiePreferenceRepository::existsByDigitalCustomerProfileId)
                .flatMap(digitalCookiePreferenceRepository::findByDigitalCustomerProfileId)
                .map(cookiePreference -> {
                    DigitalCookiePreferenceResponse response = new DigitalCookiePreferenceResponse();
                    response.setPerformanceCookie(cookiePreference.isPerformanceCookie());
                    response.setFunctionalCookie(cookiePreference.isFunctionalCookie());
                    response.setStrictlyAcceptanceCookie(cookiePreference.isStrictlyAcceptanceCookie());
                    response.setCookieCreatedBy(cookiePreference.getCookieCreatedBy());
                    response.setCookieCreationDate(cookiePreference.getCookieCreationDate());
                    Optional.ofNullable(cookiePreference.getCookieModifiedBy())
                            .filter(StringUtils::isNotBlank)
                            .ifPresent(response::setCookieModifiedBy);
                    Optional.ofNullable(cookiePreference.getCookieModificationDate())
                            .map(Object::toString)
                            .filter(StringUtils::isNotBlank)
                            .ifPresent(date -> response.setCookieModificationDate(LocalDateTime.parse(date)));
                    return Mono.just(response);
                })
                .orElseThrow(() -> new DigitalCustomerProfileIdNotFoundException(
                        Collections.singletonList(NOT_FOUND_ERROR_CODE),
                        HttpStatus.NOT_FOUND,
                        FAILURE,
                        NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId,
                        Collections.singletonList(LocalDateTime.now().format(DateTimeFormatter
                                .ofPattern(UdbConstants.DATE_FORMAT)))));
    }


    @Override
    public Mono<CheckPinStatusResponse> checkPinExistsBasedOnDigitalDeviceId(final String digitalDeviceUdid) {

        log.info("Inside checkPinExistsBasedOnCustomerDeviceId() with digitalCustomerDeviceId: {}",
                digitalDeviceUdid);

        if (digitalDeviceUdid.trim().length() > MAX_LENGTH_FIFTY) {
            throw new InvalidDigitalDeviceUdid("Invalid DigitalDeviceUdid");
        }
        String digitalDeviceUdid1 = digitalDeviceUdid.trim();

        Optional<Boolean> result = userInfoRepository.checkPinExistsBasedOnDigitalDeviceUdid(digitalDeviceUdid1);

        if (result.isPresent()) {
            Boolean pinExist = result.get();
            if (pinExist.equals(true)) {
                return Mono.just(CheckPinStatusResponse.builder()
                        .pinExists(true)
                        .build());
            } else {
                return Mono.just(CheckPinStatusResponse.builder()
                        .pinExists(false)
                        .build());
            }
        } else {
            log.warn("Invalid for DigitalDeviceUdid: {}", digitalDeviceUdid);
            throw new DigitalDeviceUdidNotFoundException(" DigitalDeviceUdid not Found: " + digitalDeviceUdid);
        }
    }

    /**
     * Checks if a user has set a PIN based on their digital customer profile ID.
     *
     * @param digitalCustomerProfileId The unique identifier of the digital customer profile.
     * @return True if the user has set a PIN, false otherwise.
     * @throws DigitalCustomerProfileIdNotFoundException if the provided digitalCustomerProfileId is not found.
     * @throws DatabaseOperationsException               if a database error occurs while checking the PIN status.
     */
    @Override
    public Boolean checkUserPinStatus(UUID digitalCustomerProfileId) {
        log.debug("Checking pin status for profile ID: {}", digitalCustomerProfileId);
        try {
            return Optional.ofNullable(
                            digitalCustomerProfileRepository.findPinSetCompletedByProfileId(digitalCustomerProfileId))
                    .orElseThrow(() ->
                            new DigitalCustomerProfileIdNotNullException("Invalid Digital Customer Profile ID"));
        } catch (DataAccessException e) {
            throw new DatabaseOperationsException(
                    "Database error occurred while checking user pin status.", e.getMessage());
        }
    }

    @Override
    public UserAPIBaseResponse saveUserPublicKeyForBioMetric(UUID digitalCustomerProfileId,
                                                             UserPublicKeyRequest userPublicKeyRequest) {
         String enablePublicKeyMessage;
         log.debug("Inside saveUserPublicKeyForBioMetric method");
         validateDeviceLinkWithUser(digitalCustomerProfileId, userPublicKeyRequest.getDeviceUUID());

         DigitalCustomerDevice device = getDigitalCustomerDevice(digitalCustomerProfileId,
                userPublicKeyRequest.getDeviceUUID());

        validateBiometricType(userPublicKeyRequest.getBiometricType());

        if (userPublicKeyRequest.getBiometricEnable().equals(true)) {
            enablePublicKeyMessage = "Public Key Saved Successfully";
        } else {
            enablePublicKeyMessage = "Biometric Disabled";
        }

        if (userPublicKeyRequest.getBiometricType().equalsIgnoreCase(FACE_ID)) {
            handleFaceId(device, userPublicKeyRequest);
        }

        if (userPublicKeyRequest.getBiometricType().equalsIgnoreCase(TOUCH_ID)) {
            handleTouchId(device, userPublicKeyRequest);
        }

        return UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .status(STATUS_MESSAGE)
                .message(enablePublicKeyMessage)
                .timeStamp(CommonUtil.getCurrentDateInUTC())
                .build();
    }

    private void validateDeviceLinkWithUser(UUID digitalCustomerProfileId, String deviceUUID) {
        UUID digitalCustomerProfileIdFromDB = digitalCustomerDeviceRepository.findDigitalCustomerProfileId(deviceUUID);

        if (!Objects.equals(digitalCustomerProfileIdFromDB, digitalCustomerProfileId)) {
            throw new UserDeviceNotLinkedException("The user is not associated with the specified device: "
                    + deviceUUID);
        }
    }

    private DigitalCustomerDevice getDigitalCustomerDevice(UUID digitalCustomerProfileId, String deviceUUID) {
        Optional<DigitalCustomerDevice> optionalDevice = digitalCustomerDeviceRepository
                .getDigitalCustomerDeviceByDeviceUUIDAndProfileID(String.valueOf(digitalCustomerProfileId), deviceUUID);

        if (optionalDevice.isEmpty()) {
            log.error("Device ID not found");
            throw new DigitalDeviceUdidNotFoundException("Device doesn't exist");
        }

        return optionalDevice.get();
    }

    private void validateBiometricType(String biometricType) {
        if (!biometricType.equalsIgnoreCase(FACE_ID) && !biometricType.equalsIgnoreCase(TOUCH_ID)) {
            log.error("Invalid biometric Type ");
            throw new InvalidArgumentException("Enter biometric Type : faceId or touchId");
        }
    }

     void handleFaceId(DigitalCustomerDevice device, UserPublicKeyRequest userPublicKeyRequest) {
        if (Boolean.TRUE.equals(userPublicKeyRequest.getBiometricEnable())) {
            if (!StringUtils.isBlank(device.getDeviceFacePublicKey())) {
                log.error("Public key already exists corresponding to the device");
                throw new UserPublicKeyAlreadyExistException("Public key already exists for FaceId");
            }
            device.setDeviceFacePublicKey(userPublicKeyRequest.getDevicePublicKey());
            log.debug("Saved the device with the public key for Face Biometric");
        } else {
            device.setDeviceFacePublicKey(null);
            log.debug("Saved the device with the null public key for Face Biometric");
        }
        digitalCustomerDeviceRepository.saveAndFlush(device);
    }

    void handleTouchId(DigitalCustomerDevice device, UserPublicKeyRequest userPublicKeyRequest) {
        if (Boolean.TRUE.equals(userPublicKeyRequest.getBiometricEnable())) {
            if (!StringUtils.isBlank(device.getDeviceTouchPublicKey())) {
                log.error("Public key already exists corresponding to the device");
                throw new UserPublicKeyAlreadyExistException("Public key already exists for TouchId");
            }
            device.setDeviceTouchPublicKey(userPublicKeyRequest.getDevicePublicKey());
            log.debug("Saved the device with the public key for Touch Biometric");
        } else {
            device.setDeviceTouchPublicKey(null);
            log.debug("Saved the device with the null public key for Touch Biometric");
        }
        digitalCustomerDeviceRepository.saveAndFlush(device);
    }


    /**
     * Saves the public key associated with a user's device PIN.
     *
     * @param publicKeyRequest An object containing the user's profile ID and device UUID for which
     *                         the public key needs to be saved.
     * @throws DigitalCustomerDeviceNotFoundException If the device associated with the provided
     *                                                profile ID and device UUID is not found.
     * @throws UserPublicKeyAlreadyExistException     If the retrieved device already has a public key set for the PIN.
     */
    @Override
    @Transactional
    public void saveUserPublicKeyForPin(PublicKeyUpdateRequest publicKeyRequest) {
        UUID profileId = publicKeyRequest.getDigitalCustomerProfile();
        String deviceUdid = publicKeyRequest.getDeviceUdid();

        log.debug("Searching for DigitalCustomerDevice with profileId: {} and deviceUUID: {}", profileId, deviceUdid);

        DigitalCustomerDevice device = Optional.ofNullable(digitalCustomerDeviceRepository
                        .findByDigitalCustomerProfileIdAndDigitalDeviceUdid(profileId, deviceUdid))
                .orElseThrow(() -> new DigitalCustomerDeviceNotFoundException(profileId));

        log.debug("Found DigitalCustomerDevice with device id: {}", device.getDigitalDeviceUdid());

        if (device.getDevicePinPublicKey() != null) {
            throw new UserPublicKeyAlreadyExistException("Public key is already exist");
        }
        Optional<DigitalCustomerProfile> existingDigitalCustomer = userInfoRepository
                .findByDigitalCustomerProfileId(profileId);
        DigitalCustomerProfile digitalCustomerProfile = existingDigitalCustomer.get();

        log.debug("Setting device pin public key for device id: {}", deviceUdid);
        device.setDevicePinPublicKey(publicKeyRequest.getDevicePublicKey());
        if (!Boolean.TRUE.equals(digitalCustomerProfile.isPinSetCompleted())) {
            digitalCustomerProfile.setPinSetCompleted(true);
            userInfoRepository.save(digitalCustomerProfile);
        }

        digitalCustomerDeviceRepository.save(device);
        log.debug("Successfully saved public key for user with profileId: {}", profileId);
    }

    @Override
    public BiometricPublicKeyResponse getUserPublicKey(String digitalDeviceUdid, String biometricType) {
        try {
            String publicKey = null;
            validateBiometricType(biometricType);
            if (biometricType.equalsIgnoreCase(FACE_ID)) {
                log.debug("Fetch public key for faceId");
                publicKey = digitalCustomerDeviceRepository.findByUserFaceAuthPublicKey(digitalDeviceUdid);
            } else if (biometricType.equalsIgnoreCase(TOUCH_ID)) {
                log.debug("Fetch public key for touchId");
                publicKey = digitalCustomerDeviceRepository.findByUserTouchAuthPublicKey(digitalDeviceUdid);
            }
            return BiometricPublicKeyResponse.builder()
                    .publicKey(validateAndReturnPublicKey(publicKey, digitalDeviceUdid))
                    .httpStatus(HttpStatus.OK)
                    .status(SUCCESS_CODE)
                    .timeStamp(new java.util.Date())
                    .build();
        } catch (DatabaseOperationsException ex) {
            log.debug("Error retrieving public key for device UDID: {}", digitalDeviceUdid);
            throw new DatabaseOperationsException(ERROR_RETRIEVING_PUBLIC_KEY_FOR_DEVICE_UDID, digitalDeviceUdid);
        }
    }

    private String validateAndReturnPublicKey(String publicKey, String digitalDeviceUdid) {
        if (StringUtils.isBlank(publicKey)) {
            log.debug("public key not found for device UDID: {}", digitalDeviceUdid);
            throw new UserPublicKeyNotFoundException(PUBLIC_KEY_NOT_FOUND_FOR_THE_GIVEN_DEVICE_UUID);
        }
        return publicKey;
    }


    private CoreCustomerProfileResponse mapToCoreCustomerProfileResponse(final Object[] coreCustomerProfile) {
        log.info(" Entered fetchUserInfo at mapToCoreCustomerProfileResponse ");

        return CoreCustomerProfileResponse.builder()
                .firstName((String) coreCustomerProfile[ZERO_CONSTANT])
                .middleName((String) coreCustomerProfile[ONE_CONSTANT])
                .lastName((String) coreCustomerProfile[TWO_CONSTANT])
                .email((String) coreCustomerProfile[THREE_CONSTANT])
                .phone((String) coreCustomerProfile[FOUR_CONSTANT])
                .build();

    }


    public UserDetailDto buildUserDetailsDTO(UserInfoResponse userInfoResponse) {
        UserDetailDto userDetailsDTO = new UserDetailDto();
        userDetailsDTO.setUserName(userInfoResponse.getDigitalUserName());
        userDetailsDTO.setUserMessage(SUCCESS);
        userDetailsDTO.setDigitalCustomerDeviceId(userInfoResponse.getDigitalCustomerDeviceId());
        return userDetailsDTO;
    }

    @Override
    public Mono<CheckMfaStatusResponse> checkMfaStatusBasedOnDigitalDeviceId(final String digitalDeviceUdid) {
        log.info("Inside checkMfaStatusBasedOnDigitalDeviceId() with digitalCustomerDeviceId: {}",
                digitalDeviceUdid);

        if (digitalDeviceUdid.trim().length() > MAX_LENGTH_FIFTY) {
            throw new InvalidDigitalDeviceUdid("Invalid DigitalDeviceUdid");
        }
        String digitalDeviceUdid1 = digitalDeviceUdid.trim();

        Optional<Boolean> result = userInfoRepository.checkMfaStatusBasedOnDigitalDeviceUdid(digitalDeviceUdid1);

        if (result.isPresent()) {
            Boolean mfaStatus = result.get();
            if (mfaStatus.equals(true)) {
                return Mono.just(CheckMfaStatusResponse.builder()
                        .mfaStatus(true)
                        .build());
            } else {
                return Mono.just(CheckMfaStatusResponse.builder()
                        .mfaStatus(false)
                        .build());
            }
        } else {
            log.warn("Invalid for DigitalDeviceUdid: {}", digitalDeviceUdid);
            throw new DigitalDeviceUdidNotFoundException(" DigitalDeviceUdid not Found: " + digitalDeviceUdid);
        }

    }

    @Override
    public String getUserPublicKeyForPin(String payloadDeviceId, String username) {

        try {
            String publicKey = digitalCustomerDeviceRepository.findByUserPublicKeyForPin(payloadDeviceId, username);
            if (StringUtils.isBlank(publicKey)) {
                log.debug("public key not for pin found for device UDID" + payloadDeviceId);
                throw new UserPublicKeyNotFoundException(PUBLIC_KEY_NOT_FOUND_FOR_THE_GIVEN_DEVICE_UUID);
            }
            return publicKey;
        } catch (DatabaseOperationsException ex) {
            log.debug("Error Retrieving public key for pin for device UDID" + payloadDeviceId);
            throw new DatabaseOperationsException(ERROR_RETRIEVING_PUBLIC_KEY_FOR_DEVICE_UDID, payloadDeviceId);
        }

    }

    @Override
    public Mono<UpdatePinStatusResponse> updatePinStatus(UUID digitalCustomerProfileId, boolean pinCompletedStatus) {
        // based on the digitalCustomerProfileId setPinStatus to true

        Optional<DigitalCustomerProfile> existingDigitalCustomer = userInfoRepository
                .findByDigitalCustomerProfileId(digitalCustomerProfileId);
        if (existingDigitalCustomer.isPresent()) {
            DigitalCustomerProfile digitalCustomer = existingDigitalCustomer.get();
            if (pinCompletedStatus != digitalCustomer.isPinSetCompleted()) {
                digitalCustomer.setPinSetCompleted(pinCompletedStatus);
                userInfoRepository.save(digitalCustomer);
            }
        } else {
            log.warn("digitalCustomerProfileId not Found: {}", digitalCustomerProfileId);
            List<String> errorCode = new ArrayList<>();
            errorCode.add(NOT_FOUND_ERROR_CODE);
            List<String> params = new ArrayList<>();
            params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT)));
            throw new DigitalCustomerProfileIdNotFoundException(errorCode,
                    HttpStatus.NOT_FOUND,
                    FAILURE,
                    NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId,
                    params);
        }
        return Mono.just(UpdatePinStatusResponse.builder()
                .pinSetCompleted(true)
                .build());
    }

    /**
     * Retrieves marketing preferences for a given digital customer profile ID.
     *
     * @param digitalCustomerProfileId the UUID of the digital customer profile
     * @return a list of marketing preference responses
     */
    @Override
    public List<MarketingPreferenceResponse> getMarketingPreferences(UUID digitalCustomerProfileId) {
        try {
            log.debug("Retrieving marketing preferences for digital customer profile ID: {}", digitalCustomerProfileId);
            Optional<DigitalMarketingNotificationPreference> marketingNotificationPreferenceOptional =
                    marketingNotificationPreferencesRepository.findByDigitalCustomerProfileId(digitalCustomerProfileId);
            List<MarketingPreferenceResponse> defaultPreference =
                    configurationServiceClient.getDefaultMarketingPreferences();

            if (marketingNotificationPreferenceOptional.isPresent()) {
                DigitalMarketingNotificationPreference userPreference = marketingNotificationPreferenceOptional.get();
                log.debug("User preferences found for digital customer profile ID: {}", digitalCustomerProfileId);
                return mergePreferences(defaultPreference, userPreference);
            } else {
                log.debug("No user preferences found for digital customer profile ID: {}. "
                        + "Returning default preferences.", digitalCustomerProfileId);
                return defaultPreference;
            }
        } catch (WebClientResponseException ex) {
            throw ex;
        } catch (Exception e) {
            throw new MarketingPreferenceException("Failed to retrieve marketing preferences", e);
        }
    }

    /**
     * Merges user preferences with the default preferences.
     *
     * @param defaults a list of default marketing preference responses
     * @param userPref the user's marketing notification preferences
     * @return a list of marketing preference responses after merging
     */
    private List<MarketingPreferenceResponse> mergePreferences(
            List<MarketingPreferenceResponse> defaults, DigitalMarketingNotificationPreference userPref) {
        log.debug("Merging user preferences with default preferences.");
        defaults.forEach(pref -> pref.setMarketingFlag(getMarketingFlag(pref.getMarketingTypeElementName(), userPref)));
        return defaults;
    }

    /**
     * Retrieves the marketing flag for a specific marketing type based on user preferences.
     *
     * @param marketingType the marketing type element name
     * @param userPref      the user's marketing notification preferences
     * @return true if the user has opted in for the given marketing type, false otherwise
     */
    private boolean getMarketingFlag(String marketingType, DigitalMarketingNotificationPreference userPref) {
        MarketingTypeEnum type = MarketingTypeEnum.fromElementName(marketingType);
        return switch (type) {
            case EMAIL -> userPref.isMarketingEmailNotification();
            case SMS -> userPref.isMarketingSmsNotification();
            case POST -> userPref.isMarketingPostNotification();
            case TELEPHONE -> userPref.isMarketingTelephoneNotification();
            case ONLINE -> userPref.isMarketingOnlineNotification();
        };
    }

    /**
     * Retrieves notification preferences for a given digital customer profile ID.
     *
     * @param digitalCustomerProfileId the UUID of the digital customer profile
     * @return a list of notification preference responses
     */
    @Override
    public List<NotificationPreferenceResponse> getNotificationPreferences(UUID digitalCustomerProfileId) {
        try {
            log.debug("Retrieving notification preferences for digital customer profile ID: {}",
                    digitalCustomerProfileId);
            Optional<DigitalNotificationPreference> bankingNotificationPreferenceOptional =
                    bankingNotificationPreferenceRepository.findByDigitalCustomerProfileId(digitalCustomerProfileId);
            List<NotificationPreferenceResponse> defaultPreference =
                    configurationServiceClient.getDefaultNotificationPreferences();

            if (bankingNotificationPreferenceOptional.isPresent()) {
                DigitalNotificationPreference userPreference = bankingNotificationPreferenceOptional.get();
                log.debug("User preferences found for digital customer profile ID: {}", digitalCustomerProfileId);
                return mergePreferences(defaultPreference, userPreference);
            } else {
                log.debug("No user preferences found for digital customer profile ID: {}. "
                        + "Returning default preferences.", digitalCustomerProfileId);
                return defaultPreference;
            }
        } catch (WebClientResponseException ex) {
            throw ex;
        } catch (Exception e) {
            throw new NotificationPreferenceException("Failed to retrieve notification preferences", e);
        }
    }

    /**
     * Merges user preferences with the default preferences.
     *
     * @param defaults a list of default notification preference responses
     * @param userPref the user's banking notification preferences
     * @return a list of notification preference responses after merging
     */
    private List<NotificationPreferenceResponse> mergePreferences(
            List<NotificationPreferenceResponse> defaults, DigitalNotificationPreference userPref) {
        log.debug("Merging user preferences with default preferences.");
        defaults.forEach(pref -> pref.setNotificationFlag(getNotificationFlag(pref.getNotificationTypeElementName(),
                userPref)));
        return defaults;
    }

    /**
     * Retrieves the notification flag for a specific notification type based on user preferences.
     *
     * @param notificationType the notification type element name
     * @param userPref the user's banking notification preferences
     * @return true if the user has opted in for the given notification type, false otherwise
     */
    private boolean getNotificationFlag(String notificationType, DigitalNotificationPreference userPref) {
        NotificationTypeEnum type = NotificationTypeEnum.fromElementName(notificationType);
        return switch (type) {
            case PUSH -> userPref.isMobilePushNotificationBanking();
            case SMS -> userPref.isSmsNotificationBanking();
            case EMAIL -> userPref.isEmailNotificationBanking();
        };
    }
}