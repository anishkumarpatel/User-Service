package com.unisys.udb.user.service;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.DigitalCustomerShortcutsRequest;
import com.unisys.udb.user.dto.request.SessionHistoryFilterRequest;
import com.unisys.udb.user.dto.response.CustomerSessionHistoryResponse;
import com.unisys.udb.user.dto.response.DigitalCustomerShortcutsResponse;
import com.unisys.udb.user.dto.response.UserAPIBaseResponse;
import com.unisys.udb.user.entity.CustomerSessionHistory;
import com.unisys.udb.user.entity.DigitalCustomerShortcuts;
import com.unisys.udb.user.exception.DigitalCustomerProfileIdNotFoundException;
import com.unisys.udb.user.exception.DigitalCustomerSessionHistoryNotFoundException;
import com.unisys.udb.user.exception.InvalidArgumentException;
import com.unisys.udb.user.repository.DigitalCustomerShortcutsRepository;
import com.unisys.udb.user.repository.SesionHistoryRepository;
import com.unisys.udb.user.repository.UserInfoRepository;
import com.unisys.udb.user.utils.masking.LogMasking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.sql.Date;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.unisys.udb.user.constants.UdbConstants.*;
import static java.util.Objects.nonNull;


@Service
@Slf4j
@Component
@RequiredArgsConstructor
public class DigitalCustomerShortcutsServiceImpl implements DigitalCustomerShortcutsService {

    private  final DigitalCustomerShortcutsRepository digitalCustomerShortcutsRepository;
    private  final SesionHistoryRepository sesionHistoryRepository;
    private final UserInfoRepository userInfoRepository;




    static void setShortcutIfNotNull(Boolean value, Consumer<Boolean> setter) {
        if (value != null && (value.equals(Boolean.TRUE) || value.equals(Boolean.FALSE))) {
            setter.accept(value);
        }
    }

    private static DigitalCustomerShortcuts setDigitalCustomerShortcuts(final UUID digitalCustomerProfileId,
                                                                        final DigitalCustomerShortcutsRequest request) {

        log.info("Inside  DigitalCustomerShortcuts method for the digital_customer_profile_id: {}",
                digitalCustomerProfileId);

        DigitalCustomerShortcuts digitalCustomerShortcuts = new DigitalCustomerShortcuts();
        if (request != null) {
            digitalCustomerShortcuts.setDigitalCustomerProfileId(digitalCustomerProfileId);
            setShortcutIfNotNull(request.getFundTransferShortcut(), digitalCustomerShortcuts::setFundTransferShortcut);
            setShortcutIfNotNull(request.getEstatementShortcut(), digitalCustomerShortcuts::setEstatementShortcut);
            setShortcutIfNotNull(request.getPayeeShortcut(), digitalCustomerShortcuts::setPayeeShortcut);
            setShortcutIfNotNull(request.getScheduledPaymentsShortcut(),
                    digitalCustomerShortcuts::setScheduledPaymentsShortcut);
            setShortcutIfNotNull(request.getCommPrefShortcut(), digitalCustomerShortcuts::setCmnctnPreferenceShortcut);
            setShortcutIfNotNull(request.getSessionHistoryShortcut(),
                    digitalCustomerShortcuts::setSessionHistoryShortcut);
            digitalCustomerShortcuts.setShortcutModificationDate(LocalDateTime.now());
            digitalCustomerShortcuts.setShortcutModifiedBy(MODIFIED_BY);
        }
        return digitalCustomerShortcuts;
    }

    @Override
    public Mono<UserAPIBaseResponse> updateDigitalCustomerShortcut(
            final UUID digitalCustomerProfileId, final DigitalCustomerShortcutsRequest request) {

        log.info("Inside UPDATE shortcuts for the digital_customer_profile_id: {}", digitalCustomerProfileId);
        Optional<DigitalCustomerShortcuts> existingShortcuts = digitalCustomerShortcutsRepository
                .findByDigitalCustomerProfileId(digitalCustomerProfileId);
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        List<String> params = new ArrayList<>();
        params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT)));
        if (existingShortcuts.isEmpty()) {
            throw new DigitalCustomerProfileIdNotFoundException(errorCode,
                    HttpStatus.NOT_FOUND,
                    FAILURE,
                    NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId,
                    params);
        } else {
            log.info("Updating Digital Customer Shortcuts for the digital_customer_profile_id: {}",
                    digitalCustomerProfileId);
            DigitalCustomerShortcuts digitalCustomerShortcuts = setDigitalCustomerShortcuts(
                    digitalCustomerProfileId, request);
            digitalCustomerShortcuts.setDigitalCustomerShortcutsId(
                    existingShortcuts.get().getDigitalCustomerShortcutsId());
            digitalCustomerShortcutsRepository.save(digitalCustomerShortcuts);
            log.info("Updated Digital Customer Shortcuts for the digital_customer_profile_id: {}",
                    digitalCustomerProfileId);
        }

        UserAPIBaseResponse response = UserAPIBaseResponse.builder()
                .httpStatus(HttpStatus.OK)
                .status("Successful!")
                .message("Digital Customer Shortcuts Updated Successfully!")
                .build();
        return Mono.just(response);
    }

    @Override
    public Mono<DigitalCustomerShortcutsResponse> getDigitalCustomerShortcut(final UUID digitalCustomerProfileId) {
        String maskedId = LogMasking.maskingDigitlProfileId(digitalCustomerProfileId);
        log.info("Inside getUserInfo() method for the digitalCustomerProfileId: {}", maskedId);
        final String userNameByDigitalCustomerProfileId = userInfoRepository
                .findUserNameByDigitalCustomerProfileId(digitalCustomerProfileId);

        final Boolean isIdExistsInDigitalTable = userInfoRepository.
                existsByDigitalCustomerProfileId(digitalCustomerProfileId);

        if (Boolean.FALSE.equals(isIdExistsInDigitalTable)) {
            throw new DigitalCustomerProfileIdNotFoundException(
                    Collections.singletonList(NOT_FOUND_ERROR_CODE),
                    HttpStatus.NOT_FOUND,
                    FAILURE,
                    NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId,
                    new ArrayList<>());
        }

        return Mono.justOrEmpty(digitalCustomerShortcutsRepository
                .findByDigitalCustomerProfileId(digitalCustomerProfileId)
                .map(digitalCustomerShortcuts -> {
                    DigitalCustomerShortcutsResponse fetchUserInfoResponse = new DigitalCustomerShortcutsResponse();
                    fetchUserInfoResponse.setPayeeShortcut(digitalCustomerShortcuts.getPayeeShortcut());
                    fetchUserInfoResponse.setCommPrefShortcut(digitalCustomerShortcuts.getCmnctnPreferenceShortcut());
                    fetchUserInfoResponse.setEStatementShortcut(digitalCustomerShortcuts.getEstatementShortcut());
                    fetchUserInfoResponse.setFundTransferShortcut(digitalCustomerShortcuts.getFundTransferShortcut());
                    fetchUserInfoResponse.setSessionHistoryShortcut(digitalCustomerShortcuts
                            .getSessionHistoryShortcut());
                    fetchUserInfoResponse.setScheduledPaymentsShortcut(digitalCustomerShortcuts
                            .getScheduledPaymentsShortcut());
                    return fetchUserInfoResponse;
                })
                .orElseGet(() -> createDefaultShortcuts(digitalCustomerProfileId, userNameByDigitalCustomerProfileId)));
    }

    private DigitalCustomerShortcutsResponse createDefaultShortcuts(UUID digitalCustomerProfileId,
                                                                    String userNameByDigitalCustomerProfileId) {
        DigitalCustomerShortcuts customerShortcuts = new DigitalCustomerShortcuts();
        customerShortcuts.setDigitalCustomerProfileId(digitalCustomerProfileId);
        customerShortcuts.setFundTransferShortcut(true);
        customerShortcuts.setPayeeShortcut(true);
        customerShortcuts.setEstatementShortcut(true);
        customerShortcuts.setSessionHistoryShortcut(false);
        customerShortcuts.setScheduledPaymentsShortcut(false);
        customerShortcuts.setCmnctnPreferenceShortcut(false);

        customerShortcuts.setShortcutModifiedBy(userNameByDigitalCustomerProfileId);
        customerShortcuts.setShortcutCreatedBy(userNameByDigitalCustomerProfileId);
        customerShortcuts.setShortcutCreationDate(UdbConstants.getCreateOrUpdateDate());
        customerShortcuts.setShortcutModificationDate(LocalDateTime.now());

        digitalCustomerShortcutsRepository.save(customerShortcuts);

        DigitalCustomerShortcutsResponse defaultResponse = new DigitalCustomerShortcutsResponse();
        defaultResponse.setPayeeShortcut(customerShortcuts.getPayeeShortcut());
        defaultResponse.setEStatementShortcut(customerShortcuts.getEstatementShortcut());
        defaultResponse.setFundTransferShortcut(customerShortcuts.getFundTransferShortcut());
        defaultResponse.setScheduledPaymentsShortcut(customerShortcuts.getScheduledPaymentsShortcut());
        defaultResponse.setCommPrefShortcut(customerShortcuts.getCmnctnPreferenceShortcut());
        defaultResponse.setSessionHistoryShortcut(customerShortcuts.getSessionHistoryShortcut());
        return defaultResponse;
    }



    @Override
    public CustomerSessionHistoryResponse getCustomerSessionHistoryResponse(UUID digitalCustomerProfileId,
                                                                            Integer offset, Integer rowCount,
                                                                            SessionHistoryFilterRequest
                                                                                        sessionHistoryFilterRequest) {
        log.debug("Inside customerSessionHistoryResponse method for the digitalCustomerProfileId: {}",
                digitalCustomerProfileId);

        if (offset < 0 || rowCount < 0) {
            throw new InvalidArgumentException("Offset and rowCount must be non-negative values.");
        }
        List<Object[]> sesionHistoryDetailsObjList = null;
        if (nonNull(sessionHistoryFilterRequest)
                && nonNull(sessionHistoryFilterRequest.getByActivity())
                && nonNull(sessionHistoryFilterRequest.getByChannel())
                && nonNull(sessionHistoryFilterRequest.getByDate())) {
            log.debug("session filter byActivity: {}, byChannel: {}, byDate: {}",
                    sessionHistoryFilterRequest.getByActivity(), sessionHistoryFilterRequest.getByChannel(),
                    sessionHistoryFilterRequest.getByDate());
            String activity = getFilterValueAsString(sessionHistoryFilterRequest.getByActivity());
            String channel = getFilterValueAsString(sessionHistoryFilterRequest.getByChannel());
            LocalDate from = getDateFilter(sessionHistoryFilterRequest, "from");
            LocalDate to = getDateFilter(sessionHistoryFilterRequest, "to");
            log.info("Filter being passed to stored procedure : Activity -> {} ; Channel -> {} ; Date -> From {} to {}",
                    activity, channel, from, to);
            sesionHistoryDetailsObjList = sesionHistoryRepository
                    .getCustomerSessionHistoryDetails(digitalCustomerProfileId, offset, rowCount, activity, channel,
                            from, to);
        } else {
            throw new InvalidArgumentException(INVALID_REQUEST_BODY);
        }
        if (nonNull(sesionHistoryDetailsObjList)) {
            List<CustomerSessionHistory> sessionHistoryList = sesionHistoryDetailsObjList.stream().map(
                            this::mapCustomerSessionHistory)
                    .toList();
            if (sesionHistoryDetailsObjList.isEmpty()) {
                throw new DigitalCustomerSessionHistoryNotFoundException(digitalCustomerProfileId);
            }
            return new CustomerSessionHistoryResponse(sessionHistoryList);
        } else {
            throw new DigitalCustomerSessionHistoryNotFoundException(digitalCustomerProfileId);
        }
    }

    private String getFilterValueAsString(Map<String, Boolean> map) {
        String filter = map.isEmpty() ? null
                : map.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(Boolean.TRUE)).map(Map.Entry::getKey)
                        .collect(Collectors.joining(","));
        filter = nonNull(filter) && filter.equalsIgnoreCase("") ? null : filter;
        return filter;
    }

    private LocalDate getDateFilter(SessionHistoryFilterRequest sessionHistoryFilterRequest, String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(INPUT_DATE_PATTERN);
        if (!sessionHistoryFilterRequest.getByDate().isEmpty()
                && sessionHistoryFilterRequest.getByDate().containsKey(value)
                && !sessionHistoryFilterRequest.getByDate().get(value).isBlank()) {
            return LocalDate.parse(sessionHistoryFilterRequest.getByDate().get(value), formatter);

        } else {
            throw new InvalidArgumentException("byDate should contain from and to date");
        }
    }

    private CustomerSessionHistory mapCustomerSessionHistory(Object[] sessionHistoryDetails) {
        String activityTime = Optional.ofNullable(sessionHistoryDetails[TWO_CONSTANT]).orElse("").toString();
        if (!activityTime.isEmpty()) {
            LocalTime time24 = LocalTime.parse(activityTime, DateTimeFormatter.ofPattern(TIME_PATTERN_24H));
            DateTimeFormatter time12Formatter = DateTimeFormatter.ofPattern(TIME_PATTERN_12H);
            activityTime = time24.format(time12Formatter);
        }
        LocalDate date = Optional.ofNullable(sessionHistoryDetails[ONE_CONSTANT]).isEmpty() ? LocalDate.now()
                : ((Date) sessionHistoryDetails[ONE_CONSTANT]).toLocalDate();
        String formattedDate = date.format(DateTimeFormatter.ofPattern(OUTPUT_DATE_PATTERN));
        return new CustomerSessionHistory(
                Optional.ofNullable(sessionHistoryDetails[ZERO_CONSTANT]).isEmpty() ? ""
                        : (String) sessionHistoryDetails[ZERO_CONSTANT], // activity Name
                formattedDate, // activityDate
                activityTime, // activityTime
                Optional.ofNullable(sessionHistoryDetails[THREE_CONSTANT]).isEmpty() ? ""
                        : (String) sessionHistoryDetails[THREE_CONSTANT], // activityChannel
                Optional.ofNullable(sessionHistoryDetails[FOUR_CONSTANT]).isEmpty() ? ""
                        : (String) sessionHistoryDetails[FOUR_CONSTANT] //activityPlatform
        );
    }


}