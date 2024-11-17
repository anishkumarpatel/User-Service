package com.unisys.udb.user.service;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.DigitalCookiesPreferenceRequest;
import com.unisys.udb.user.dto.request.DigitalCustomerProfileDTO;
import com.unisys.udb.user.entity.*;
import com.unisys.udb.user.exception.DigitalCustomerDeviceNotFoundException;
import com.unisys.udb.user.repository.*;
import com.unisys.udb.user.service.impl.UserInfoServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.unisys.udb.user.constants.UdbConstants.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class DigitalCustomerProfileAndDeviceInjector {

    private final UserInfoServiceImpl userInfoServiceImpl;

    private final DigitalCustomerProfileRepository digitalCustomerProfileRepository;
    private final DigitalCustomerDeviceRepository digitalCustomerDeviceRepository;
    private final DigitalDeviceLinkRepository digitalDeviceLinkRepository;
    private final DigitalCookiePreferenceRepository digitalCookiePreferenceRepository;

    private final DigitalCustomerDeviceAuditRepository digitalCustomerDeviceAuditRepository;

    @Transactional
    public void insertDigitalProfileDeviceLink(final DigitalCustomerProfileDTO dto) {
        log.info("Inserting digital profile-device link for digital customer profile ID: {}",
                dto.getDigitalCustomerProfileId());
        DigitalCustomerProfile digitalCustomerProfile = new DigitalCustomerProfile();
        digitalCustomerProfile.setDigitalCustomerProfileId(UUID.fromString(dto.getDigitalCustomerProfileId()));
        digitalCustomerProfile.setCoreCustomerProfileId(UUID.fromString(dto.getCoreCustomerProfileId()));
        digitalCustomerProfile.setDigitalCustomerStatusTypeId(UdbConstants.DIGITAL_CUSTOMER_STATUS_TYPE_ID);
        digitalCustomerProfile.setDigitalUserName(dto.getDigitalUserName());
        digitalCustomerProfile.setRegistrationDate(LocalDateTime.now());
        digitalCustomerProfile.setProfileCreatedBy(dto.getDigitalUserName());
        digitalCustomerProfile.setProfileModificationDate(null);
        digitalCustomerProfile.setProfileModifiedBy(null);
        digitalCustomerProfile.setMfaActivityCompleted(true);
        long pinExpiryPeriod = userInfoServiceImpl.getGlobalConfigValue("pin");
        digitalCustomerProfile.setPinExpiryDate(LocalDateTime.now().plusDays(pinExpiryPeriod));
        long passwordExpiryPeriod = userInfoServiceImpl.getGlobalConfigValue("password");
        digitalCustomerProfile.setPwdExpiryDate(LocalDateTime.now().plusDays(passwordExpiryPeriod));
        digitalCustomerProfileRepository.saveAndFlush(digitalCustomerProfile);
        getCustomerDevice(dto, digitalCustomerProfile);
        saveCookies(dto);

    }

    @Transactional
    public void getDigitalCustomerDevice(DigitalCustomerProfileDTO dto,
                                         DigitalCustomerProfile digitalCustomerProfile) {
        getCustomerDevice(dto, digitalCustomerProfile);
    }

    public void getCustomerDevice(DigitalCustomerProfileDTO dto, DigitalCustomerProfile digitalCustomerProfile) {
        DigitalCustomerDevice digitalCustomerDevice = new DigitalCustomerDevice();
        digitalCustomerDevice.setDigitalDeviceUdid(dto.getDigitalDeviceUdid());
        digitalCustomerDevice.setDeviceName(dto.getDeviceName());
        digitalCustomerDevice.setDeviceType(dto.getDeviceType());
        digitalCustomerDevice.setDeviceStatus(true);
        digitalCustomerDevice.setDeviceOsVersion(dto.getDeviceOsVersion());
        digitalCustomerDevice.setDeviceCreationDate(LocalDateTime.now());
        digitalCustomerDevice.setDeviceCreatedBy(dto.getDigitalUserName());
        digitalCustomerDevice.setDeviceModificationDate(null);
        digitalCustomerDevice.setDeviceModifiedBy(null);
        digitalCustomerDevice.setStrictlyAcceptanceCookie(true);
        digitalCustomerDevice.setPerformanceCookie(true);
        digitalCustomerDevice.setFunctionalCookie(true);

        DigitalDeviceLink deviceLink = new DigitalDeviceLink();
        deviceLink.setDigitalCustomerProfile(digitalCustomerProfile);
        deviceLink.setDeviceLinkRegisterFlag(true);
        deviceLink.setDigitalCustomerDevice(digitalCustomerDevice);
        deviceLink.setDeviceLinkCreationDate(LocalDateTime.now());


        digitalCustomerDeviceRepository.saveAndFlush(digitalCustomerDevice);
        digitalDeviceLinkRepository.saveAndFlush(deviceLink);

        saveDeviceAudit(dto, digitalCustomerProfile, digitalCustomerDevice, deviceLink);
    }

    public void saveCookies(DigitalCustomerProfileDTO dto) {
        Optional<DigitalCustomerDevice> digitalCustomerDevice = digitalCustomerDeviceRepository.findByDigitalDeviceUdid(
                dto.getDigitalDeviceUdid());
        if (digitalCustomerDevice.isEmpty()) {
            log.error("Digital Customer Device Not found ");
            throw new DigitalCustomerDeviceNotFoundException("Digital Customer Device Not Found");
        }
        log.info("Retrieving cookie required parameters based on digital device udid: {}",
                digitalCustomerDevice.get().getDigitalDeviceUdid());
        DigitalCookiesPreferenceRequest cookiesPreferenceRequest = new DigitalCookiesPreferenceRequest();
        if (digitalCustomerDevice.get().getDigitalDeviceUdid() != null) {
            List<Object[]> result = digitalCustomerDeviceRepository.retrieveCookiePreferenceByDeviceUdid(
                    digitalCustomerDevice.get().getDigitalDeviceUdid());
            if (!result.isEmpty()) {
                Object[] cookiePreference = result.get(0);
                cookiesPreferenceRequest.setStrictlyAcceptanceCookie((Boolean) cookiePreference[ZERO_CONSTANT]);
                cookiesPreferenceRequest.setPerformanceCookie((Boolean) cookiePreference[ONE_CONSTANT]);
                cookiesPreferenceRequest.setFunctionalCookie((Boolean) cookiePreference[TWO_CONSTANT]);
                log.info("Successfully retrieved required cookie parameters based on digital device udid: {}",
                        digitalCustomerDevice.get().getDigitalDeviceUdid());
            }
        }

        DigitalCookiePreference digitalCookiePreference = new DigitalCookiePreference();
        if (cookiesPreferenceRequest.getStrictlyAcceptanceCookie() != null
                && cookiesPreferenceRequest.getFunctionalCookie() != null
                && cookiesPreferenceRequest.getPerformanceCookie() != null) {
            log.info("Setting up cookie parameters and saving data into digital cookie preference table");
            digitalCookiePreference.setDigitalCustomerProfileId(UUID.fromString(dto.getDigitalCustomerProfileId()));
            digitalCookiePreference.setStrictlyAcceptanceCookie(cookiesPreferenceRequest.getStrictlyAcceptanceCookie());
            digitalCookiePreference.setFunctionalCookie(cookiesPreferenceRequest.getFunctionalCookie());
            digitalCookiePreference.setPerformanceCookie(cookiesPreferenceRequest.getPerformanceCookie());
            digitalCookiePreference.setCookieCreationDate(LocalDateTime.now());
            digitalCookiePreference.setCookieCreatedBy("Nirbikar");
            digitalCookiePreferenceRepository.saveAndFlush(digitalCookiePreference);
            log.info("Successfully saved cookie parameters into digital cookie preference table");
        }
    }


    public void saveDeviceAudit(DigitalCustomerProfileDTO dto, DigitalCustomerProfile digitalCustomerProfile,
                                DigitalCustomerDevice digitalCustomerDevice, DigitalDeviceLink deviceLink) {
        log.info("Inserting the data into device Link audit table");
        DigitalCustomerDeviceAudit digitalCustomerDeviceAudit = new DigitalCustomerDeviceAudit();
        digitalCustomerDeviceAudit.setDigitalCustomerProfile(digitalCustomerProfile);
        digitalCustomerDeviceAudit.setDigitalCustomerDevice(digitalCustomerDevice);
        digitalCustomerDeviceAudit.setDeviceAuditTypeRefId(1);
        digitalCustomerDeviceAudit.setDigitalDeviceLink(deviceLink);
        digitalCustomerDeviceAudit.setDeviceLinkRegisteredFlagAudit(deviceLink.getDeviceLinkRegisterFlag());
        digitalCustomerDeviceAudit.setDeviceLinkAuditCreatedBy(dto.getDigitalUserName());
        digitalCustomerDeviceAudit.setDeviceLinkAuditCreationDate(LocalDateTime.now());
        digitalCustomerDeviceAuditRepository.insertData(digitalCustomerDeviceAudit);
        log.info("Digital profile-device link inserted successfully for digital customer profile ID: {}",
                dto.getDigitalCustomerProfileId());
    }

}

