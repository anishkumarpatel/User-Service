package com.unisys.udb.user.service;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.DigitalCookiesPreferenceRequest;
import com.unisys.udb.user.dto.response.DigitalCookiesPreferenceResponse;
import com.unisys.udb.user.entity.DigitalCookiePreference;
import com.unisys.udb.user.exception.DigitalCustomerProfileIdNotFoundException;
import com.unisys.udb.user.repository.DigitalCookiePreferenceRepository;
import com.unisys.udb.user.repository.DigitalCustomerProfileRepository;
import com.unisys.udb.user.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.unisys.udb.user.constants.UdbConstants.*;


@Service
@Slf4j

@RequiredArgsConstructor
public class DigitalCookiesPreferenceServiceImpl implements DigitalCookiesPreferenceService {

    private final DigitalCookiePreferenceRepository digitalCookiesPreferenceRepository;

    private final DigitalCustomerProfileRepository digitalCustomerProfileRepository;

    private final UserInfoRepository userInfoRepository;

    @Override
    public Mono<DigitalCookiesPreferenceResponse>
    saveDigitalCookiesPreferences(UUID digitalCustomerProfileId, DigitalCookiesPreferenceRequest
            cookiesPreferenceRequest) {
        log.info("Inside save digital cookies preference for the digital_customer_profile_id: {}",
                digitalCustomerProfileId);
        Boolean isDigitalCustomerProfileId = digitalCustomerProfileRepository
                .existsByDigitalCustomerProfileId(digitalCustomerProfileId);

        if (Boolean.FALSE.equals(isDigitalCustomerProfileId)) {
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
        log.info("Saving Digital Cookies Preference for the digital_customer_profile_id: {}",
                digitalCustomerProfileId);

        DigitalCookiePreference digitalCookiePreference1 = getDigitalCookiePreference(
                digitalCustomerProfileId);
        DigitalCookiePreference digitalCookiesPreference = setDigitalCookiesPreference(
                digitalCustomerProfileId, cookiesPreferenceRequest, digitalCookiePreference1);
        digitalCookiesPreferenceRepository.save(digitalCookiesPreference);
        log.info("Updated Digital Cookies Preference for the digital_customer_profile_id: {}",
                digitalCustomerProfileId);
        DigitalCookiesPreferenceResponse response = DigitalCookiesPreferenceResponse.builder()
                .message("Digital Cookies Preference Data Updated successfully")
                .timestamp(LocalDateTime.now())
                .httpStatus(HttpStatus.OK)
                .build();
        log.info("Exiting   DigitalCookiesPreferenceServiceImpl  method of saveDigitalCookiesPreferences");
        return Mono.just(response);
    }

    public DigitalCookiePreference setDigitalCookiesPreference(
            UUID digitalCustomerProfileId, DigitalCookiesPreferenceRequest cookiesPreferenceRequest,
            DigitalCookiePreference digitalCookiesPreference) {
        final String userNameByDigitalCustomerProfileId = userInfoRepository
                .findUserNameByDigitalCustomerProfileId(digitalCustomerProfileId);
        digitalCookiesPreference.setDigitalCustomerProfileId(digitalCustomerProfileId);
        digitalCookiesPreference.setCookieModificationDate(LocalDateTime.now());
        digitalCookiesPreference.setFunctionalCookie(cookiesPreferenceRequest.getFunctionalCookie());
        digitalCookiesPreference.setPerformanceCookie(cookiesPreferenceRequest.getPerformanceCookie());
        digitalCookiesPreference.setCookieModifiedBy(userNameByDigitalCustomerProfileId);
        digitalCookiesPreference.setCookieCreatedBy(userNameByDigitalCustomerProfileId);
        digitalCookiesPreference.setCookieCreationDate(LocalDateTime.now());
        digitalCookiesPreference.setStrictlyAcceptanceCookie(cookiesPreferenceRequest.
                getStrictlyAcceptanceCookie());

        log.info("Exiting   DigitalCookiesPreferenceServiceImpl  method of setDigitalCookiesPreferences");
        return digitalCookiesPreference;
    }

    public DigitalCookiePreference getDigitalCookiePreference(UUID digitalCustomerProfileId) {
        log.info("Inside the getDigitalCookiePreference method of user service for the  customer profile id {}",
                digitalCustomerProfileId);
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        List<String> params = new ArrayList<>();
        params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT)));
        return digitalCookiesPreferenceRepository.findByDigitalCustomerProfileId(digitalCustomerProfileId)
                .orElseThrow(() -> new DigitalCustomerProfileIdNotFoundException(errorCode,
                        HttpStatus.NOT_FOUND,
                        FAILURE,
                        NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId,
                        params));
    }
}
