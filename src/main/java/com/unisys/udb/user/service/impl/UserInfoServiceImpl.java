package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.config.AppConfig;
import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.BiometricStatusDTO;
import com.unisys.udb.user.dto.request.DeviceTokenRequest;
import com.unisys.udb.user.dto.request.DigitalAlertRequest;
import com.unisys.udb.user.dto.request.DigitalPwdRequest;
import com.unisys.udb.user.dto.request.NotificationOrchestratorRequest;
import com.unisys.udb.user.dto.request.TermsConditionsAndCookiesRequest;
import com.unisys.udb.user.dto.request.UpdateExpiryDTO;
import com.unisys.udb.user.dto.request.UserDetailDto;
import com.unisys.udb.user.dto.response.CustomerDetail;
import com.unisys.udb.user.dto.response.CustomerDetailsResponse;
import com.unisys.udb.user.dto.response.DeRegisterDevicesResponse;
import com.unisys.udb.user.dto.response.DeviceDataForRegisterDevice;
import com.unisys.udb.user.dto.response.DeviceInfoResponse;
import com.unisys.udb.user.dto.response.DigitalCustomerPwdResponse;
import com.unisys.udb.user.dto.response.GetTermsConditionAndCookiesInfoResponse;
import com.unisys.udb.user.dto.response.GlobalConfigResponse;
import com.unisys.udb.user.dto.response.NotificationOrchestratorResponse;
import com.unisys.udb.user.dto.response.TermsConditionsAndCookieResponse;
import com.unisys.udb.user.dto.response.UpdateExpiryResponse;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.dto.response.UserInfoResponse;
import com.unisys.udb.user.dto.response.UserLockResponse;
import com.unisys.udb.user.dto.response.UserStatusResponse;
import com.unisys.udb.user.dto.response.UserSuccessResponse;
import com.unisys.udb.user.entity.CountryValidation;
import com.unisys.udb.user.entity.DigitalCustomerDevice;
import com.unisys.udb.user.entity.DigitalCustomerDeviceAudit;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import com.unisys.udb.user.entity.DigitalCustomerPwd;
import com.unisys.udb.user.entity.DigitalDeviceLink;
import com.unisys.udb.user.exception.ConfigurationServiceException;
import com.unisys.udb.user.exception.ConfigurationServiceUnavailableException;
import com.unisys.udb.user.exception.CustomerNotFoundException;
import com.unisys.udb.user.exception.DatabaseOperationsException;
import com.unisys.udb.user.exception.DigitalCustomerDeviceNotFoundException;
import com.unisys.udb.user.exception.DigitalCustomerProfileIdNotFoundException;
import com.unisys.udb.user.exception.DigitalDeviceUdidNotFoundException;
import com.unisys.udb.user.exception.DigitalPasswordStorageException;
import com.unisys.udb.user.exception.InvalidArgumentException;
import com.unisys.udb.user.exception.InvalidDataException;
import com.unisys.udb.user.exception.InvalidRequestException;
import com.unisys.udb.user.exception.InvalidUpdateField;
import com.unisys.udb.user.exception.InvalidUserException;
import com.unisys.udb.user.exception.MissingRequiredRequestParamException;
import com.unisys.udb.user.exception.PasswordExpiryException;
import com.unisys.udb.user.exception.PublishNotificationFailureException;
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
import com.unisys.udb.user.service.UserInfoService;
import com.unisys.udb.user.service.client.ConfigurationServiceClient;
import com.unisys.udb.user.service.client.NotificationOrchestratorServiceClient;
import com.unisys.udb.user.utils.dto.response.CommonUtil;
import com.unisys.udb.user.utils.dto.response.NotificationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.unisys.udb.user.constants.UdbConstants.ACCOUNT_MULTIPLE_ATTEMPT_LOCKED;
import static com.unisys.udb.user.constants.UdbConstants.ACTIVE;
import static com.unisys.udb.user.constants.UdbConstants.ANOTHER_LOCK_ATTEMPT;
import static com.unisys.udb.user.constants.UdbConstants.CONFIGURATION_SERVICE_RETRY_MSG;
import static com.unisys.udb.user.constants.UdbConstants.DATABASE_ERROR_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.DEVICE_TABLE_UPDATE_SUCCESS;
import static com.unisys.udb.user.constants.UdbConstants.DIGITAL_ACCESS_LOCKED;
import static com.unisys.udb.user.constants.UdbConstants.ENSURE_CREDENTIALS;
import static com.unisys.udb.user.constants.UdbConstants.ERROR_UPDATING_EXPIRY_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.FAILURE;
import static com.unisys.udb.user.constants.UdbConstants.FAILURE_RESPONSE_TYPE;
import static com.unisys.udb.user.constants.UdbConstants.FIVE_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.FOUR_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.INCORRECT_CREDENTIALS;
import static com.unisys.udb.user.constants.UdbConstants.INTERNAL_SERVER_ERROR_CODE;
import static com.unisys.udb.user.constants.UdbConstants.INVALID_REQUEST_PAYLOAD;
import static com.unisys.udb.user.constants.UdbConstants.INVALID_UPDATE_TYPE_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.INVALID_USER;
import static com.unisys.udb.user.constants.UdbConstants.LANGUAGE_PREFERENCE;
import static com.unisys.udb.user.constants.UdbConstants.NOTIFICATION_ACTIVITY;
import static com.unisys.udb.user.constants.UdbConstants.NOT_FOUND_ERROR_CODE;
import static com.unisys.udb.user.constants.UdbConstants.OK_RESPONSE_CODE;
import static com.unisys.udb.user.constants.UdbConstants.ONE_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.PASSWORD;
import static com.unisys.udb.user.constants.UdbConstants.PASSWORD_EXPIRY_DATE_UPDATED_SUCCESSFULLY;
import static com.unisys.udb.user.constants.UdbConstants.PIN;
import static com.unisys.udb.user.constants.UdbConstants.PIN_EXPIRY_DATE_UPDATED_SUCCESSFULLY;
import static com.unisys.udb.user.constants.UdbConstants.SERVICE_UNAVAILABLE;
import static com.unisys.udb.user.constants.UdbConstants.SIX_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.STATUS_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.THREE_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.THREE_INCORRECT_CREDENTIALS;
import static com.unisys.udb.user.constants.UdbConstants.TWO_CONSTANT;
import static com.unisys.udb.user.constants.UdbConstants.UDB_PIN_EXPRY_PERIOD;
import static com.unisys.udb.user.constants.UdbConstants.UDB_PWD_EXPRY_PERIOD;
import static com.unisys.udb.user.constants.UdbConstants.UNEXPECTED_ERROR_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.UNLOCK_PENDING;
import static com.unisys.udb.user.constants.UdbConstants.UPDATE_COOKIES;
import static com.unisys.udb.user.constants.UdbConstants.UPDATE_TERMS_CONDITIONS;
import static com.unisys.udb.user.constants.UdbConstants.USER_LOCKED;
import static com.unisys.udb.user.constants.UdbConstants.USER_LOCKED_SUCCESS_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.USER_SERVICE;
import static com.unisys.udb.user.constants.UdbConstants.ZERO_CONSTANT;


@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("squid:S00107")
public class UserInfoServiceImpl implements UserInfoService {

    private final DigitalCustomerDeviceRepository digitalCustomerDeviceRepository;
    private final DigitalDeviceLinkRepository digitalDeviceLinkRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final DigitalCustomerPwdRepository digitalCustomerPwdRepository;
    private final AppConfig appConfig;
    private final UserInfoRepository userInfoRepository;
    private final DigitalCustomerProfileRepository digitalCustomerProfileRepository;
    private final DigitalCustomerAlertService digitalCustomerAlertService;
    private final NotificationOrchestratorServiceClient notificationOrchestratorServiceClient;
    private final DigitalCustomerDeviceAuditRepository digitalCustomerDeviceAuditRepository;
    private final ConfigurationServiceClient configurationServiceClient;
    private final PinRepository pinRepository;
    private final NotificationUtil notificationUtil;
    private KieContainer kieContainer;


    @Autowired
    @SuppressWarnings("squid:S00107")
    public UserInfoServiceImpl(DigitalCustomerDeviceRepository digitalCustomerDeviceRepository,
                               DigitalDeviceLinkRepository digitalDeviceLinkRepository,
                               LoginAttemptRepository loginAttemptRepository,
                               DigitalCustomerPwdRepository digitalCustomerPwdRepository,
                               AppConfig appConfig, UserInfoRepository userInfoRepository,
                               DigitalCustomerProfileRepository digitalCustomerProfileRepository,
                               DigitalCustomerAlertService digitalCustomerAlertService, KieContainer kieContainer,
                               NotificationOrchestratorServiceClient notificationOrchestratorServiceClient,
                               DigitalCustomerDeviceAuditRepository digitalCustomerDeviceAuditRepository,
                               ConfigurationServiceClient configurationServiceClient, PinRepository pinRepository,
                               NotificationUtil notificationUtil) {
        this.digitalCustomerDeviceRepository = digitalCustomerDeviceRepository;
        this.digitalDeviceLinkRepository = digitalDeviceLinkRepository;
        this.loginAttemptRepository = loginAttemptRepository;
        this.digitalCustomerPwdRepository = digitalCustomerPwdRepository;
        this.appConfig = appConfig;
        this.userInfoRepository = userInfoRepository;
        this.digitalCustomerProfileRepository = digitalCustomerProfileRepository;
        this.digitalCustomerAlertService = digitalCustomerAlertService;
        this.kieContainer = kieContainer;
        this.notificationOrchestratorServiceClient = notificationOrchestratorServiceClient;
        this.digitalCustomerDeviceAuditRepository = digitalCustomerDeviceAuditRepository;
        this.configurationServiceClient = configurationServiceClient;
        this.pinRepository = pinRepository;
        this.notificationUtil = notificationUtil;
    }


    @Override
    public UserInfoResponse getUserInfoResponse(String digitalDeviceUdid) {
        UserInfoResponse userInfoResponse = UserInfoResponse.builder().build();
        Optional<DigitalCustomerDevice> digitalCustomerDevice = digitalCustomerDeviceRepository
                .findByDigitalDeviceUdid(digitalDeviceUdid);
        if (digitalCustomerDevice.isEmpty()) {
            log.info("Device info is not found");
            return userInfoResponse;
        }
        log.debug("Device info is found, Device name {}", digitalCustomerDevice.get().getDeviceName());
        DigitalDeviceLink digitalDeviceLink = digitalDeviceLinkRepository
                .findByDigitalCustomerDevice(digitalCustomerDevice.get());
        if (Objects.isNull(digitalDeviceLink)) {
            log.debug("Device link entity is not found");
            return userInfoResponse;
        }
        log.info("Device link entity is found");
        DigitalCustomerProfile digitalCustomerProfile = digitalDeviceLink.getDigitalCustomerProfile();
        userInfoResponse.setDigitalCustomerProfileId(digitalCustomerProfile.getDigitalCustomerProfileId());
        userInfoResponse.setDigitalUserName(digitalCustomerProfile.getDigitalUserName());
        userInfoResponse.setDigitalCustomerStatusTypeId(digitalCustomerProfile.getDigitalCustomerStatusTypeId());
        userInfoResponse.setCoreCustomerProfileId(digitalCustomerProfile.getCoreCustomerProfileId());
        userInfoResponse.setDigitalCustomerDeviceId(digitalDeviceUdid);
        return userInfoResponse;
    }


    @Override
    public DeviceInfoResponse getUserDeviceInfo(final UUID digitalCustomerProfileId) {
        log.info("Fetching device info for digital customer profile ID: {}", digitalCustomerProfileId);


        List<DigitalCustomerStatus> result = digitalCustomerDeviceRepository
                .findByProfileIdAndStatus(digitalCustomerProfileId);
        return result.stream()
                .findFirst()
                .map(digitalCustomerDevice -> DeviceInfoResponse.builder()
                        .digitalCustomerDeviceId(digitalCustomerDevice.getDeviceId())
                        .deviceType(digitalCustomerDevice.getDeviceType())
                        .deviceToken(digitalCustomerDevice.getDeviceToken())
                        .build())
                .orElseThrow(() -> {
                    log.warn("Device info not found for digital customer profile ID: {}", digitalCustomerProfileId);
                    return new DigitalCustomerDeviceNotFoundException(digitalCustomerProfileId);
                });
    }

    @Override
    public Mono<UserAPIBaseResponse> updateDeviceToken(DeviceTokenRequest request) {

        log.debug("Inside UPDATE device Token for the digitalDeviceUdid: {}",
                request.getDigitalDeviceUdId());

        if (StringUtils.isBlank(request.getDigitalDeviceUdId()) || StringUtils.isBlank(request.getDeviceToken())) {
            throw new InvalidArgumentException(INVALID_REQUEST_PAYLOAD);
        }

        DigitalCustomerDevice digitalCustomerDevice = digitalCustomerDeviceRepository
                .findByDigitalDeviceUdid(request.getDigitalDeviceUdId())
                .orElseThrow(() -> new DigitalDeviceUdidNotFoundException(request.getDigitalDeviceUdId()));

        if (digitalCustomerDevice != null) {
            digitalCustomerDevice.setDeviceToken(request.getDeviceToken());
            digitalCustomerDeviceRepository.saveAndFlush(digitalCustomerDevice);
            log.info("Device Token Updated successfully for digital Device Id: {}",
                    digitalCustomerDevice.getDigitalCustomerDeviceId());
        }

        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .status(STATUS_MESSAGE)
                .message("Device Token Updated Successfully")
                .timeStamp(new Date())
                .build();
        return Mono.just(response);
    }


    public void updateFailureAttemptDetailsByUsername(UserDetailDto userDetailDto) {
        Integer maxAttemptCount = appConfig.getLoginCount();

        String userName = userDetailDto.getUserName();
        Optional.ofNullable(userName)
                .filter(String::isEmpty)
                .map(name -> getUserInfoResponse(userDetailDto.getDigitalCustomerDeviceId()))
                .map(UserInfoResponse::getDigitalUserName)
                .ifPresent(userDetailDto::setUserName);
        log.debug("username ::{} and msg is ::{} ", userDetailDto.getUserName(),
                userDetailDto.getUserMessage());


        Integer loginAttemptCount = loginAttemptRepository.updateLoginAttemptDetails(userDetailDto.getUserName(),
                userDetailDto.getUserMessage(),
                userDetailDto.getDigitalCustomerDeviceId(), maxAttemptCount);
        log.debug("The login Attempt count is ::{}", loginAttemptCount);

        // Initialize error codes
        List<String> errorCodes = new ArrayList<>();
        if (loginAttemptCount > 0) {
            saveCustomerAlert(userName, UdbConstants.ALERT_INVALID_LOGIN_ATTEMPT);
            if (loginAttemptCount >= maxAttemptCount) {
                try {
                    notificationUtil.sendNotification(notificationUtil.prepareRequiredFieldsMap(null,
                                    userDetailDto.getUserName(), USER_SERVICE,
                                    DIGITAL_ACCESS_LOCKED, "UserLocked Template", LANGUAGE_PREFERENCE),
                            new HashMap<>());
                } catch (Exception e) {
                    errorCodes.add("errorPublishNotificationFailureException");
                }
                errorCodes.add(ACCOUNT_MULTIPLE_ATTEMPT_LOCKED);
                throw new UserLockedException(errorCodes, HttpStatus.LOCKED, FAILURE, USER_LOCKED,
                        Collections.emptyList());
            } else if (loginAttemptCount == maxAttemptCount - 1) {
                List<String> params = new ArrayList<>();
                errorCodes.add(THREE_INCORRECT_CREDENTIALS);
                errorCodes.add(ANOTHER_LOCK_ATTEMPT);
                errorCodes.add(ENSURE_CREDENTIALS);
                throw new InvalidUserException(errorCodes, HttpStatus.UNAUTHORIZED, FAILURE, INVALID_USER, params);
            } else if (loginAttemptCount <= maxAttemptCount - 2) {
                errorCodes.add(INCORRECT_CREDENTIALS);
                throw new InvalidUserException(errorCodes, HttpStatus.UNAUTHORIZED, FAILURE,
                        INVALID_USER, Collections.emptyList());
            }
        }
    }

    @Override
    public Mono<UserStatusResponse> getUserStatus(String digitalUserName) {
        log.debug("Inside the get User Status with the digital_customer_profile_id: {}",
                digitalUserName);

        Optional<Object> customerStatusType = userInfoRepository
                .getCustomerStatusTypeByDigitalUserName(digitalUserName);
        if (customerStatusType.isEmpty()) {
            log.debug("digitalCustomerProfileId not found in Database");
            throw new UserNameNotFoundException(digitalUserName);
        }

        return Mono.just(userStatusResponse(customerStatusType.get().toString(), digitalUserName));
    }

    private UserStatusResponse userStatusResponse(String userStatus, String userName) {
        log.debug("Fetching the password expiration date");

        DigitalCustomerProfile digitalCustomerProfile = Optional.ofNullable(userInfoRepository
                .findByDigitalUserName(userName)).orElseThrow(
                () -> new UserNameNotFoundException("Invalid username"));

        boolean isPasswordExpired = digitalCustomerProfile.getPwdExpiryDate().isBefore(LocalDateTime.now());
        boolean isPinExpired = digitalCustomerProfile.getPinExpiryDate().isBefore(LocalDateTime.now());

        return UserStatusResponse
                .builder()
                .accountStatus(userStatus)
                .isPasswordExpired(isPasswordExpired)
                .isPinExpired(isPinExpired)
                .build();
    }

    @Override
    @Transactional
    public Mono<TermsConditionsAndCookieResponse> updateTermsConditionsAndCookies(
            String deviceId, String updateField,
            TermsConditionsAndCookiesRequest request) {

        CommonUtil.validateMandatoryFieldsTermsAndConditions(request, updateField);

        Optional<DigitalCustomerDevice> digitalCustomerDevice = digitalCustomerDeviceRepository
                .findByDigitalDeviceUdid(deviceId);
        if (Objects.isNull(digitalCustomerDevice)) {
            log.info("Device info is not found");
        }

        switch (updateField) {
            case UPDATE_TERMS_CONDITIONS:
                return Mono.just(handleTermsConditionUpdate(request, digitalCustomerDevice));
            case UPDATE_COOKIES:
                return Mono.just(handleCookies(request, digitalCustomerDevice));
            default:
                log.error("Invalid update fields");
                throw new InvalidUpdateField("Invalid updateFields!");
        }

    }

    @Override
    public ResponseEntity<Mono<GetTermsConditionAndCookiesInfoResponse>> getTermsConditionAndCookiesInfoByDeviceId(
            String deviceId) {
        if (null == deviceId || deviceId.trim().isEmpty()) {
            throw new MissingRequiredRequestParamException("deviceId");
        }
        return digitalCustomerDeviceRepository.findByDigitalDeviceUdid(deviceId)
                .map(digitalCustomerDevice ->
                        ResponseEntity.ok(Mono.just(GetTermsConditionAndCookiesInfoResponse.builder()
                                .termsAndConditions(digitalCustomerDevice.getTermsAndConditions())
                                .functionalCookie(digitalCustomerDevice.getFunctionalCookie())
                                .performanceCookie(digitalCustomerDevice.getPerformanceCookie())
                                .strictlyAcceptanceCookie(digitalCustomerDevice.getStrictlyAcceptanceCookie())
                                .build()))
                ).orElseThrow(() -> new DigitalCustomerDeviceNotFoundException(UUID.fromString(deviceId)));
    }

    public TermsConditionsAndCookieResponse handleTermsConditionUpdate(TermsConditionsAndCookiesRequest request,

                                                                       Optional<DigitalCustomerDevice> deviceById) {

        updateTermsAndConditions(request, deviceById);
        TermsConditionsAndCookieResponse response = TermsConditionsAndCookieResponse.builder()
                .message(DEVICE_TABLE_UPDATE_SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        log.debug("updateTermsAndConditions :: Request End");
        return response;
    }

    public void updateTermsAndConditions(TermsConditionsAndCookiesRequest request,
                                         Optional<DigitalCustomerDevice> deviceById) {
        Boolean termsAndConditions = request.getTermsConditions();
        deviceById.ifPresent(device -> {
            device.setTermsAndConditions(termsAndConditions);
            digitalCustomerDeviceRepository.save(device);
        });
    }

    public TermsConditionsAndCookieResponse handleCookies(TermsConditionsAndCookiesRequest request,
                                                          Optional<DigitalCustomerDevice> deviceById) {

        updateCookies(request, deviceById);
        TermsConditionsAndCookieResponse response = TermsConditionsAndCookieResponse.builder()
                .message(DEVICE_TABLE_UPDATE_SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();

        log.debug("updateCookies :: Request End");
        return response;
    }

    public void updateCookies(TermsConditionsAndCookiesRequest request, Optional<DigitalCustomerDevice> deviceById) {

        Boolean strictlyAcceptanceCookie = request.getStrictlyAcceptanceCookie();
        Boolean functionalCookie = request.getFunctionalCookie();
        Boolean performanceCookie = request.getPerformanceCookie();

        deviceById.ifPresent(device -> {
            device.setStrictlyAcceptanceCookie(strictlyAcceptanceCookie);
            device.setFunctionalCookie(functionalCookie);
            device.setPerformanceCookie(performanceCookie);
            digitalCustomerDeviceRepository.save(device);
        });
    }


    public void sendNotification(final Map<String, String> requiredFieldsMap,
                                 final Map<String, String> extendedFieldsMap) {
        try {
            NotificationOrchestratorRequest notificationRequest = new NotificationOrchestratorRequest();
            notificationRequest.setRequiredFieldsMap(requiredFieldsMap);
            notificationRequest.setExtendedFieldsMap(extendedFieldsMap);
            log.info("Invoking notification orchestrator service api for sending notification");
            NotificationOrchestratorResponse orchestratorResponse = notificationOrchestratorServiceClient
                    .publishNotification(notificationRequest);
            log.info("step = sendNotification, action = Notification status for the activity {} : {}",
                    requiredFieldsMap.get(NOTIFICATION_ACTIVITY), orchestratorResponse);
        } catch (WebClientResponseException exception) {
            log.error("Failed to send notification due to {}", exception.getResponseBodyAsString());
            throw new PublishNotificationFailureException(exception.getResponseBodyAsString());
        }
    }

    @Override
    public CustomerDetailsResponse getCustomerDetailsBySearchTerm(String searchTerm) {
        log.debug("Inside getCustomerDetailsBySearchTerm() to get customer details for search query: {}",
                searchTerm);
        if (StringUtils.isBlank(searchTerm)) {
            throw new InvalidArgumentException(INVALID_REQUEST_PAYLOAD);
        }
        List<Object[]> details = userInfoRepository.findCustomerDetailsBySearchTerm(searchTerm);
        if (details.isEmpty()) {
            throw new CustomerNotFoundException("No customer found the given search query :" + searchTerm);
        }
        List<CustomerDetail> customerDetailList = details.stream().map(this::mapCustomerDetails)
                .toList();
        return new CustomerDetailsResponse(customerDetailList);
    }

    private CustomerDetail mapCustomerDetails(Object[] usersDetails) {
        return new CustomerDetail(
                Optional.ofNullable(usersDetails[ZERO_CONSTANT]).isEmpty() ? ""
                        : (String) usersDetails[ZERO_CONSTANT],
                Optional.ofNullable(usersDetails[ONE_CONSTANT]).isEmpty() ? ""
                        : (String) usersDetails[ONE_CONSTANT],
                Optional.ofNullable(usersDetails[TWO_CONSTANT]).isEmpty() ? UUID.fromString("")
                        : UUID.fromString((String) usersDetails[TWO_CONSTANT]),
                Optional.ofNullable(usersDetails[THREE_CONSTANT]).isEmpty() ? ""
                        : (String) usersDetails[THREE_CONSTANT],
                Optional.ofNullable(usersDetails[FOUR_CONSTANT]).isEmpty() ? Timestamp.valueOf("")
                        : (Timestamp) usersDetails[FOUR_CONSTANT],
                Optional.ofNullable(usersDetails[FIVE_CONSTANT]).isEmpty() ? ""
                        : (String) usersDetails[FIVE_CONSTANT],
                Optional.ofNullable(usersDetails[SIX_CONSTANT]).isEmpty() ? ""
                        : (String) usersDetails[SIX_CONSTANT]);
    }

    public UserSuccessResponse validateUserName(String userName) throws DataAccessException {

        log.debug("Validating user name");
        boolean isExists = digitalCustomerProfileRepository.existsByDigitalUserName(userName);

        if (!isExists) {
            throw new InvalidRequestException("Invalid username", userName);
        }

        log.debug("User name valid");
        return new UserSuccessResponse("User name is valid");
    }

    @Transactional
    @Override
    public DeRegisterDevicesResponse deRegisterDevices(UUID digitalCustomerProfileId, List<String> devicesUdidList) {
        log.debug("Inside the deRegisterDevice method, calling CommonUtil class");
        List<Integer> customerDeviceIds = digitalCustomerDeviceRepository.getListOfDeviceCustomerIds(devicesUdidList);
        List<Integer> registeredCustomerDevicesIds = digitalDeviceLinkRepository
                .getRegisteredCustomerDevicesIds(digitalCustomerProfileId);
        if (customerDeviceIds.isEmpty()) {
            log.error("No Devices found for De-Register");
            throw new DigitalCustomerDeviceNotFoundException(digitalCustomerProfileId);
        }
        for (Integer devicesIds : customerDeviceIds) {
            if (!registeredCustomerDevicesIds.contains(devicesIds)) {
                log.error(devicesIds + "Is not linked with user");
                throw new DigitalCustomerDeviceNotFoundException(digitalCustomerProfileId);
            }
        }
        try {
            log.debug("Update the device link registered flag to false");
            int countDevicesToBeDeRegistered = digitalDeviceLinkRepository.deRegisteredDevices(customerDeviceIds);
            int countBiometricToBeNull = digitalCustomerDeviceRepository
                    .updateBiometricPublicKeyNull(customerDeviceIds);
            if (countDevicesToBeDeRegistered > 0 && countBiometricToBeNull > 0) {
                log.debug("Updated Successfully device link registered flag in device link table");
                updateDeviceLinkAudiTable(digitalCustomerProfileId, devicesUdidList);
            }
        } catch (Exception exception) {
            log.error("Unable to update the registered flag", exception);
            throw new DatabaseOperationsException("Unable to update the registered flag");
        }
        return new DeRegisterDevicesResponse(OK_RESPONSE_CODE, LocalDateTime.now(),
                "De-Registered Successfully");
    }

    private void updateDeviceLinkAudiTable(UUID digitalCustomerProfileId, List<String> customerDeviceId) {
        try {
            Optional<DigitalCustomerProfile> digitalCustomerProfileOptional = digitalCustomerProfileRepository
                    .findById(digitalCustomerProfileId);
            if (digitalCustomerProfileOptional.isEmpty()) {
                log.error("Digital Customer Profile not found");
                return;
            }
            // To get the Device object
            List<DigitalCustomerDevice> digitalCustomerDeviceList = digitalCustomerDeviceRepository
                    .getListOfDevicesByDevicesIds(customerDeviceId);
            List<Integer> digitalCustomerDeviceIds = digitalCustomerDeviceList.stream()
                    .map(DigitalCustomerDevice::getDigitalCustomerDeviceId)
                    .toList();
            // To get the device link object
            List<DigitalDeviceLink> digitalDeviceLinks = digitalDeviceLinkRepository
                    .getListOfDevicesLinkByDevices(digitalCustomerDeviceIds);
            log.debug("Inserting the data into digital_device_link_audit table");

            for (int i = 0; i < digitalCustomerDeviceList.size(); i++) {
                DigitalCustomerDeviceAudit digitalCustomerDeviceAudit = new DigitalCustomerDeviceAudit();
                digitalCustomerDeviceAudit.setDigitalCustomerProfile(digitalCustomerProfileOptional.get());
                digitalCustomerDeviceAudit.setDigitalCustomerDevice(digitalCustomerDeviceList.get(i));
                digitalCustomerDeviceAudit.setDeviceAuditTypeRefId(1);
                digitalCustomerDeviceAudit.setDigitalDeviceLink(digitalDeviceLinks.get(i));
                digitalCustomerDeviceAudit.setDeviceLinkRegisteredFlagAudit(digitalDeviceLinks.get(i)
                        .getDeviceLinkRegisterFlag());
                digitalCustomerDeviceAudit.setDeviceLinkAuditCreatedBy(
                        digitalCustomerProfileOptional.get().getDigitalUserName());
                digitalCustomerDeviceAudit.setDeviceLinkAuditCreationDate(LocalDateTime.now());
                digitalCustomerDeviceAuditRepository.insertData(digitalCustomerDeviceAudit);
            }
            log.debug("Data has been inserted into the digital device link audit table ");
        } catch (Exception exception) {
            log.error("Database operations Exception", exception);
            throw new DatabaseOperationsException("Database operations Exception");
        }
    }

    public DigitalCustomerPwdResponse storeOldPassword(DigitalPwdRequest digitalPwdRequest) {
        DigitalCustomerPwdResponse response = new DigitalCustomerPwdResponse();
        try {
            String encryptedPassword = new BCryptPasswordEncoder().encode(digitalPwdRequest.getPassword());

            DigitalCustomerPwd digitalCustomerPwd = new DigitalCustomerPwd();
            digitalCustomerPwd.setDigitalCustomerProfileId(digitalPwdRequest.getDigitalProfileId());
            digitalCustomerPwd.setEncryptedOldPassword(encryptedPassword);

            LocalDateTime currentDate = LocalDateTime.now();
            Date currentDateAsDate = java.sql.Timestamp.valueOf(currentDate);
            // Set the dates and createdBy field
            digitalCustomerPwd.setPasswordChangeDate(currentDateAsDate);
            digitalCustomerPwd.setPasswordExpiryDate(currentDateAsDate);
            digitalCustomerPwd.setPasswordCreationDate(currentDateAsDate);
            digitalCustomerPwd.setPasswordCreatedBy(userInfoRepository.
                    findUserNameByDigitalCustomerProfileId(
                            digitalPwdRequest.getDigitalProfileId()));

            // Leave modification date and modifiedBy fields null

            digitalCustomerPwd = digitalCustomerPwdRepository.save(digitalCustomerPwd);

            response.setDigitalProfileId(digitalCustomerPwd.getDigitalCustomerProfileId());
            response.setMessage("Old password stored successfully.");
            log.info("Old password stored successfully: {}", digitalPwdRequest.toString());
        } catch (Exception e) {
            log.error("Error storing old password.", digitalPwdRequest.toString(), e);
            throw new DigitalPasswordStorageException("Error storing old password.");
        }
        return response;
    }

    @Override
    public List<DeviceDataForRegisterDevice> getAllRegisterDevice(
            UUID digitalCustomerProfileId, boolean registered) {
        log.debug("Inside the getAllRegisterDevice method, calling CommonUtil class");
        List<String> errorCode = new ArrayList<>();
        List<String> params = new ArrayList<>();
        params.add(LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        if (Boolean.FALSE.equals(digitalCustomerProfileRepository
                .existsByDigitalCustomerProfileId(digitalCustomerProfileId))) {
            log.error("Invalid digital customer profile ID: " + digitalCustomerProfileId);
            errorCode.add(NOT_FOUND_ERROR_CODE);
            throw new DigitalCustomerProfileIdNotFoundException(errorCode, HttpStatus.NOT_FOUND,
                    "Invalid digital customer profile ID: "
                            + digitalCustomerProfileId, FAILURE, params);
        }
        try {
            List<Object[]> devices;

            if (Boolean.TRUE.equals(registered)) {
                devices = digitalCustomerDeviceRepository
                        .findRegisteredDevicesByDigitalCustomerProfileIdAndDeviceStatus(
                                digitalCustomerProfileId
                        );
            } else {
                devices = digitalCustomerDeviceRepository
                        .findDeRegisteredDevicesByDigitalCustomerProfileIdAndDeviceStatus(
                                digitalCustomerProfileId
                        );
            }
            return devices.stream()
                    .map(this::mapToDeviceDataForRegisterDevice)
                    .toList();
        } catch (Exception e) {
            log.error("Unable to retrieve the information");
            throw new DataAccessException("Unable to retrieve the information of device") {
            };
        }


    }

    private DeviceDataForRegisterDevice mapToDeviceDataForRegisterDevice(Object[] data) {
        return DeviceDataForRegisterDevice.builder()
                .digitalCustomerProfileId(UUID.fromString((String) data[ZERO_CONSTANT]))
                .deviceId((int) data[ONE_CONSTANT])
                .deviceName((String) data[TWO_CONSTANT])
                .creationDate(((Timestamp) data[THREE_CONSTANT]).toLocalDateTime())
                .modificationDate(data[FOUR_CONSTANT] == null ? null
                        : ((Timestamp) data[FOUR_CONSTANT]).toLocalDateTime())
                .registeredFlag((Boolean) (data[FIVE_CONSTANT]))
                .digitalDeviceUUId(((String) data[SIX_CONSTANT]))
                .build();
    }

    @Override
    public String updateBiometricStatus(BiometricStatusDTO request, UUID customerProfileId) {
        log.debug("Inside the updateBiometricStatus method");
        return CommonUtil.updateBiometricStatus(request, customerProfileId);
    }

    private void saveCustomerAlert(String userName, String alertKey) {
        try {
            UUID profileId = digitalCustomerProfileRepository.findDigitalCustomerProfileIdByUserName(userName);
            DigitalAlertRequest alertRequest = new DigitalAlertRequest();
            alertRequest.setDigitalCustomerProfileId(profileId);
            alertRequest.setAlertKey(alertKey);
            digitalCustomerAlertService.saveDigitalCustomerAlert(alertRequest);
        } catch (Exception e) {
            log.error("Error occurred while saving customer alert: {}", ExceptionUtils.getStackTrace(e));
        }
    }

    public CountryValidation getRules(CountryValidation applicantRequest) {

        log.debug("getRules {}", applicantRequest);
        CountryValidation countryValidation = new CountryValidation();
        KieSession kieSession = kieContainer.newKieSession();
        kieSession.setGlobal("countryValidation", countryValidation);
        kieSession.insert(applicantRequest);
        kieSession.fireAllRules();
        kieSession.dispose();
        return countryValidation;
    }

    @Override
    public List<String> getBroadCastReferenceId(UUID digitalCustmerProfileId) {
        List<String> broadCastReferenceIdIdList = userInfoRepository.getBroadCastReferenceId(digitalCustmerProfileId);
        if (broadCastReferenceIdIdList != null && !broadCastReferenceIdIdList.isEmpty()) {
            log.debug("The broadcast reference id =" + broadCastReferenceIdIdList);
            return broadCastReferenceIdIdList;
        }
        return Collections.emptyList();
    }

    private List<Object> handleWebClientException(WebClientResponseException webClientException) {
        List<String> params = new ArrayList<>();
        params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT)));
        if (webClientException.getMessage().contains(SERVICE_UNAVAILABLE)) {
            throw new ConfigurationServiceUnavailableException(SERVICE_UNAVAILABLE,
                    HttpStatus.SERVICE_UNAVAILABLE, FAILURE_RESPONSE_TYPE,
                    CONFIGURATION_SERVICE_RETRY_MSG, params);
        } else if (webClientException.getMessage().contains(INTERNAL_SERVER_ERROR_CODE)) {
            throw new ConfigurationServiceException(
                    INTERNAL_SERVER_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR, FAILURE_RESPONSE_TYPE,
                    CONFIGURATION_SERVICE_RETRY_MSG, params);
        } else {
            UdbExceptionResponse exceptionResponse = webClientException.getResponseBodyAs(UdbExceptionResponse.class);
            throw new WebClientIntegrationException(exceptionResponse, webClientException.getStatusCode());
        }
    }

    /**
     * Updates the expiry date for a user based on the provided updateExpiryDTO.
     *
     * @param updateExpiryDTO The updateExpiryDTO containing the username and update type.
     * @return The UpdateExpiryResponse containing the updated username and a success message.
     */
    @Override
    public UpdateExpiryResponse updateExpiry(UpdateExpiryDTO updateExpiryDTO) {
        log.debug("Entering updateExpiry method with updateExpiryDTO: {}", updateExpiryDTO);

        // Fetch the global config value for expiry period
        long expiryPeriod = getGlobalConfigValue(updateExpiryDTO.getUpdateType());
        log.debug("Fetched global config value for expiry period: {}", expiryPeriod);

        // Fetch the user profile using Optional and method reference
        DigitalCustomerProfile profile = Optional.ofNullable(userInfoRepository.findByDigitalUserName(
                        updateExpiryDTO.getUsername()))
                .orElseThrow(() -> new UserNameNotFoundException(
                        "User not found with username: " + updateExpiryDTO.getUsername()
                ));
        log.debug("Fetched user profile for username: {}", updateExpiryDTO.getUsername());

        //If the user is unlocked in CAH (unlock pending),we need to make the user status active during reset PIN
       if  (PIN.equalsIgnoreCase(updateExpiryDTO.getUpdateType())) {
           profile.setDigitalCustomerStatusTypeId(
                   profile.getDigitalCustomerStatusTypeId() == UNLOCK_PENDING
                           ? ACTIVE : profile.getDigitalCustomerStatusTypeId()
           );
           profile.setPinSetCompleted(true);
       }
        // Set expiry date based on global configuration
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(expiryPeriod);
        log.debug("Calculated expiry date: {}", expiryDate);

        // Update the expiry based on the updateType using lambda
        Optional.ofNullable(getUpdateExpiryAction(updateExpiryDTO.getUpdateType(), profile, expiryDate))
                .ifPresent(Runnable::run);
        log.debug("Updated expiry date for user: {}", updateExpiryDTO.getUsername());

        try {
            // Save the updated profile back to the database
            digitalCustomerProfileRepository.save(profile);
            log.debug("Saved updated profile to database");

            // Return success response with specific message
            String successMessage = updateExpiryDTO.getUpdateType().equalsIgnoreCase(PASSWORD)
                    ? PASSWORD_EXPIRY_DATE_UPDATED_SUCCESSFULLY : PIN_EXPIRY_DATE_UPDATED_SUCCESSFULLY;
            return new UpdateExpiryResponse(updateExpiryDTO.getUsername(), successMessage);
        } catch (Exception ex) {
            throw new PasswordExpiryException(ERROR_UPDATING_EXPIRY_MESSAGE);
        }
    }

    /**
     * Retrieves the global config value for the expiry period.
     *
     * @param updateType The type of update (password or PIN)
     * @return The global config value for the expiry period.
     */
    public long getGlobalConfigValue(String updateType) {
        log.debug("Entering getGlobalConfigValue method");
        List<GlobalConfigResponse> globalConfigResponses = new ArrayList<>();
        try {
            String configKey = updateType.equalsIgnoreCase(PASSWORD) ? UDB_PWD_EXPRY_PERIOD : UDB_PIN_EXPRY_PERIOD;
            globalConfigResponses = configurationServiceClient.getGlobalConfig(configKey);
            log.debug("Fetched global config responses for {}", configKey);
        } catch (WebClientResponseException ex) {
            handleWebClientException(ex);
        }
        log.debug("Returning global config value");
        return Long.parseLong(globalConfigResponses.get(0).getGlobalConfigValue());
    }

    /**
     * Retrieves the update expiry action based on the provided update type, profile, and expiry date.
     *
     * @param updateType The update type.
     * @param profile    The user profile.
     * @param expiryDate The expiry date.
     * @return The update expiry action.
     */
    private Runnable getUpdateExpiryAction(String updateType, DigitalCustomerProfile profile,
                                           LocalDateTime expiryDate) {
        log.debug("Entering getUpdateExpiryAction method with updateType, profile, expiryDate");
        return switch (updateType.toLowerCase()) {
            case PASSWORD -> () -> profile.setPwdExpiryDate(expiryDate);
            case PIN -> () -> profile.setPinExpiryDate(expiryDate);
            default -> throw new InvalidDataException(
                    INVALID_UPDATE_TYPE_MESSAGE
            );
        };
    }
    @Override
    @Transactional
    public UserLockResponse lockUserAccount(UUID digitalCustomerProfileId) {
        List<String> errorCodes = new ArrayList<>();
        try {
            int updatedRows = digitalCustomerProfileRepository.lockUserAccount(digitalCustomerProfileId);
            log.debug("Attempted to lock account {}. Rows updated: {}", digitalCustomerProfileId, updatedRows);

            sendLockNotification(digitalCustomerProfileId, errorCodes);

            return new UserLockResponse(USER_LOCKED_SUCCESS_MESSAGE, HttpStatus.OK.value());
        } catch (DataAccessException e) {
            return new UserLockResponse(DATABASE_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR.value());
        } catch (Exception e) {
            return new UserLockResponse(UNEXPECTED_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    private void sendLockNotification(UUID digitalCustomerProfileId, List<String> errorCodes) {
        try {
            notificationUtil.sendNotification(
                    notificationUtil.prepareRequiredFieldsMap(
                            digitalCustomerProfileId,
                            null,
                            USER_SERVICE,
                            DIGITAL_ACCESS_LOCKED,
                            "UserLocked Template",
                            LANGUAGE_PREFERENCE
                    ),
                    new HashMap<>()
            );
        } catch (Exception e) {
            errorCodes.add("errorPublishNotificationFailureException");
        }
    }
}