package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.SessionHistoryFilterRequest;
import com.unisys.udb.user.dto.request.UserStatusServiceRequest;
import com.unisys.udb.user.dto.response.CustomerSessionHistoryResponse;
import com.unisys.udb.user.dto.response.CustomerStatusUpdateReasonResponse;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import com.unisys.udb.user.entity.DigitalCustomerStatusTypeRef;
import com.unisys.udb.user.exception.DigitalCustomerProfileIdNotFoundException;
import com.unisys.udb.user.exception.DigitalCustomerStatusTypeRefException;
import com.unisys.udb.user.exception.NotificationFailure;
import com.unisys.udb.user.exception.UserStatusException;
import com.unisys.udb.user.exception.StatusUpdateReasonNotFoundException;
import com.unisys.udb.user.repository.DigitalCustomerProfileRepository;
import com.unisys.udb.user.repository.DigitalCustomerStatusTypeRefRepository;
import com.unisys.udb.user.repository.InternalReasonRefRepository;
import com.unisys.udb.user.service.CAHService;
import com.unisys.udb.user.service.DigitalCustomerShortcutsService;
import com.unisys.udb.user.utils.dto.response.CommonUtil;
import com.unisys.udb.user.utils.dto.response.NotificationUtil;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.unisys.udb.user.constants.UdbConstants.ACTIVE;
import static com.unisys.udb.user.constants.UdbConstants.DEACTIVATED_ACTION;
import static com.unisys.udb.user.constants.UdbConstants.DEACTIVATE_ERROR;
import static com.unisys.udb.user.constants.UdbConstants.DEACTIVATE_USER;
import static com.unisys.udb.user.constants.UdbConstants.DIGITAL_ACCESS_UNLOCKED;
import static com.unisys.udb.user.constants.UdbConstants.DIGITAL_CUSTOMER_PROFILEID;
import static com.unisys.udb.user.constants.UdbConstants.FAILURE;
import static com.unisys.udb.user.constants.UdbConstants.LANGUAGE_PREFERENCE;
import static com.unisys.udb.user.constants.UdbConstants.LOCKED;
import static com.unisys.udb.user.constants.UdbConstants.NOT_FOUND_ERROR_CODE;
import static com.unisys.udb.user.constants.UdbConstants.NOT_FOUND_ERROR_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.STATUS_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.STATUS_TYPE_NOT_FOUND_ERROR_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.STEP;
import static com.unisys.udb.user.constants.UdbConstants.SUSPENDED;
import static com.unisys.udb.user.constants.UdbConstants.SUSPENDED_ACTION;
import static com.unisys.udb.user.constants.UdbConstants.SUSPEND_ERROR;
import static com.unisys.udb.user.constants.UdbConstants.SUSPEND_USER;
import static com.unisys.udb.user.constants.UdbConstants.UNLOCK_ERROR;
import static com.unisys.udb.user.constants.UdbConstants.UNLOCK_USER;
import static com.unisys.udb.user.constants.UdbConstants.UNSUSPENDED_ACTION;
import static com.unisys.udb.user.constants.UdbConstants.UNSUSPEND_ERROR;
import static com.unisys.udb.user.constants.UdbConstants.UNSUSPEND_USER;
import static com.unisys.udb.user.constants.UdbConstants.USER_ACCOUNT_STATUS_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.USER_ACCOUNT_STATUS_UNLOCK_MESSAGE;
import static com.unisys.udb.user.constants.UdbConstants.USER_SERVICE;
import static com.unisys.udb.user.constants.UdbConstants.DIGITAL_PROFILE_NOT_FOUND;
import static com.unisys.udb.user.constants.UdbConstants.ADMIN;

@Service
@Slf4j
@Component
@RequiredArgsConstructor
public class CAHServiceImpl implements CAHService {

    private final DigitalCustomerProfileRepository digitalCustomerProfileRepository;
    private final DigitalCustomerStatusTypeRefRepository digitalCustomerStatusTypeRefRepository;
    private final InternalReasonRefRepository internalReasonRefRepository;
    private final NotificationUtil notificationUtil;
    private final DigitalCustomerShortcutsService digitalCustomerShortcutsService;

    @Override
    public Mono<UserAPIBaseResponse> unSuspendDigitalBankingAccess(
            final UserStatusServiceRequest userStatusServiceRequest,
            final DigitalCustomerProfile digitalCustomerProfile, final Integer statusTypeRefId) {
        UUID digitalCustomerProfileId = userStatusServiceRequest.getDigitalCustomerProfileId();
        log.info(STEP + " = " + UNSUSPEND_USER + ", "
                + "action = Inside unsuspend digital banking access status for "
                + "digital customer profile id :{}", digitalCustomerProfileId);
        Integer digitalCustomerStatus = digitalCustomerProfile.getDigitalCustomerStatusTypeId();
        if (digitalCustomerStatus.equals(SUSPENDED)) {
            LocalDateTime modifiedTime = LocalDateTime.now();
            digitalCustomerProfileRepository.updateDigitalCustomerStatus(digitalCustomerProfileId, statusTypeRefId,
                    userStatusServiceRequest.getReason(), ADMIN, modifiedTime);
            log.info(STEP + " = " + UNSUSPEND_USER + ", "
                            + "action = Digital account has been unsuspended successfully for: {}",
                    digitalCustomerProfileId);
            return buildUserAPIBaseResponse(UNSUSPENDED_ACTION, digitalCustomerProfileId);
        } else {
            throw new UserStatusException(UNSUSPEND_ERROR, digitalCustomerProfileId);
        }
    }

    private Mono<UserAPIBaseResponse> buildUserAPIBaseResponse(final String actionMessage,
                                                               final UUID digitalCustomerProfileId) {
        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .status(STATUS_MESSAGE)
                .message(String.format(USER_ACCOUNT_STATUS_MESSAGE, actionMessage, digitalCustomerProfileId))
                .timeStamp(CommonUtil.getCurrentDateInUTC())
                .build();
        return Mono.just(response);
    }

    @Override
    public Mono<UserAPIBaseResponse> suspendDigitalBankingAccess(
            final UserStatusServiceRequest userStatusServiceRequest,
            final DigitalCustomerProfile digitalCustomerProfile, final Integer statusTypeRefId) {
        UUID digitalCustomerProfileId = userStatusServiceRequest.getDigitalCustomerProfileId();
        log.info(STEP + " = " + SUSPEND_USER + ", "
                + "action = Inside suspend digital banking access status for "
                + DIGITAL_CUSTOMER_PROFILEID + " :{}", digitalCustomerProfileId);
        Integer digitalCustomerStatus = digitalCustomerProfile.getDigitalCustomerStatusTypeId();
        if (digitalCustomerStatus.equals(ACTIVE)) {
            LocalDateTime modifiedTime = LocalDateTime.now();
            digitalCustomerProfileRepository.updateDigitalCustomerStatus(digitalCustomerProfileId, statusTypeRefId,
                    userStatusServiceRequest.getReason(), ADMIN, modifiedTime);
            log.info(STEP + " = suspendDigitalBankingAccess, "
                            + "action = Digital account has been suspended successfully for: {}",
                    digitalCustomerProfileId);
            return buildUserAPIBaseResponse(SUSPENDED_ACTION, digitalCustomerProfileId);
        } else {
            throw new UserStatusException(SUSPEND_ERROR, digitalCustomerProfileId);
        }
    }
    @Override
    public Mono<UserAPIBaseResponse> deactivateDigitalBankingAccess(
            final UserStatusServiceRequest userStatusServiceRequest,
            final DigitalCustomerProfile digitalCustomerProfile, final Integer statusTypeRefId) {
        UUID digitalCustomerProfileId = userStatusServiceRequest.getDigitalCustomerProfileId();
        log.info("step = " + DEACTIVATE_USER + ", "
                + "action = Inside deactivate digital banking access status for "
                + DIGITAL_CUSTOMER_PROFILEID + " :{}", digitalCustomerProfileId);
        Integer digitalCustomerStatus = digitalCustomerProfile.getDigitalCustomerStatusTypeId();
        if (digitalCustomerStatus.equals(ACTIVE) || digitalCustomerStatus.equals(SUSPENDED)) {
            LocalDateTime modifiedTime = LocalDateTime.now();
            digitalCustomerProfileRepository.updateDigitalCustomerStatus(digitalCustomerProfileId, statusTypeRefId,
                    userStatusServiceRequest.getReason(), ADMIN, modifiedTime);
            log.info("step = deactivateDigitalBankingAccess, "
                            + "action = Digital account has been deactivated successfully for: {}",
                    digitalCustomerProfileId);
            return buildUserAPIBaseResponse(DEACTIVATED_ACTION, digitalCustomerProfileId);
        } else {
            throw new UserStatusException(DEACTIVATE_ERROR, digitalCustomerProfileId);
        }
    }

    @Override
    @Retry(name = "orchestratorApiRetry")
    public Mono<UserAPIBaseResponse> unlockDigitalBankingAccess(
            final UserStatusServiceRequest userStatusServiceRequest,
            final DigitalCustomerProfile digitalCustomerProfile, final Integer statusTypeRefId) {
        UUID digitalCustomerProfileId = userStatusServiceRequest.getDigitalCustomerProfileId();
        log.info("step = " + UNLOCK_USER + ", "
                + "action = Inside unlock digital banking access status for "
                + DIGITAL_CUSTOMER_PROFILEID + " :{}", digitalCustomerProfileId);
        Integer digitalCustomerStatus = digitalCustomerProfile.getDigitalCustomerStatusTypeId();
        if (digitalCustomerStatus.equals(LOCKED)) {
            LocalDateTime modifiedTime = LocalDateTime.now();
            digitalCustomerProfileRepository.updateDigitalCustomerStatus(digitalCustomerProfileId, statusTypeRefId,
                    userStatusServiceRequest.getReason(), ADMIN, modifiedTime);
            log.info("step = unlockDigitalBankingAccess, "
                    + "action = Digital account unlock request has been received successfully for: {}",
                    digitalCustomerProfileId);
            try {
                    notificationUtil.sendNotification(notificationUtil.prepareRequiredFieldsMap(
                            digitalCustomerProfileId, null, USER_SERVICE, DIGITAL_ACCESS_UNLOCKED,
                            "UserUnlocked Template", LANGUAGE_PREFERENCE), new HashMap<>());
            } catch (Exception e) {
                throw new NotificationFailure("User unlocked successfully but failed to send notification");
            }
            UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .status(STATUS_MESSAGE)
                    .message(String.format(USER_ACCOUNT_STATUS_UNLOCK_MESSAGE, digitalCustomerProfileId))
                    .timeStamp(CommonUtil.getCurrentDateInUTC())
                    .build();
            return Mono.just(response);
        } else {
            throw new UserStatusException(UNLOCK_ERROR, digitalCustomerProfileId);
        }

    }

    public DigitalCustomerProfile getCustomerStatus(final UUID digitalCustomerProfileId) {
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        List<String> params = new ArrayList<>();
        params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT)));
        return digitalCustomerProfileRepository
                .findById(digitalCustomerProfileId)
                .orElseThrow(() -> new DigitalCustomerProfileIdNotFoundException(errorCode,
                        HttpStatus.NOT_FOUND,
                        FAILURE,
                        NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId,
                        params));
    }
    public Integer getDigitalCustomerStatusTypeRefId(final String customerStatusType) {
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        List<String> params = new ArrayList<>();
        params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT)));
        DigitalCustomerStatusTypeRef digitalStatus = digitalCustomerStatusTypeRefRepository
                .findByCustomerStatusTypeIgnoreCase(customerStatusType)
                .orElseThrow(() -> new DigitalCustomerStatusTypeRefException(errorCode,
                        HttpStatus.NOT_FOUND,
                        FAILURE,
                        STATUS_TYPE_NOT_FOUND_ERROR_MESSAGE + customerStatusType,
                        params));
        return digitalStatus.getDigitalCustomerStatusTypeRefId();
    }
    public CustomerSessionHistoryResponse getCustomerActivityHistory(final UUID coreCustomerProfileId,
                                                                     Integer offset, Integer rowCount,
                                                                     SessionHistoryFilterRequest
                                                                             sessionHistoryFilterRequest) {
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        List<String> params = new ArrayList<>();
        params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT)));
        DigitalCustomerProfile profile = digitalCustomerProfileRepository
                .findByCoreCustomerProfileId(coreCustomerProfileId)
                .orElseThrow(() -> new DigitalCustomerProfileIdNotFoundException(errorCode,
                        HttpStatus.NOT_FOUND,
                        FAILURE,
                        DIGITAL_PROFILE_NOT_FOUND + coreCustomerProfileId,
                        params));
        return digitalCustomerShortcutsService.getCustomerSessionHistoryResponse(profile.getDigitalCustomerProfileId(),
                offset, rowCount, sessionHistoryFilterRequest);

    }

    @Override
    public CustomerStatusUpdateReasonResponse getReasonsForCustomerStatusUpdate(final String status) {
        List<String> reasons = internalReasonRefRepository.findReasonNameByReasonCategoryIgnoreCase(status);
        if (reasons.isEmpty()) {
            throw new StatusUpdateReasonNotFoundException("No reason found in the database to update account status to "
                    + "the specified state : " + status);
        }
        return CustomerStatusUpdateReasonResponse.builder().reasonCategory(status).reasonDetailsList(reasons).build();
    }
}
