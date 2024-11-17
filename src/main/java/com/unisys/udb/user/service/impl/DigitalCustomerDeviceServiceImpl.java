package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.dto.response.BiometricStatusResponse;
import com.unisys.udb.user.repository.CustomerContactInfo;
import com.unisys.udb.user.dto.response.DeviceRegistrationResponseDTO;
import com.unisys.udb.user.dto.response.DigitalCustomerDeviceResponse;
import com.unisys.udb.user.entity.DigitalCustomerDevice;
import com.unisys.udb.user.exception.CustomerContactNotFoundException;
import com.unisys.udb.user.exception.DigitalCustomerDeviceNotFoundException;
import com.unisys.udb.user.repository.DigitalCustomerDeviceRepository;
import com.unisys.udb.user.service.DigitalCustomerDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
public class DigitalCustomerDeviceServiceImpl implements DigitalCustomerDeviceService {

    private final DigitalCustomerDeviceRepository digitalCustomerDeviceRepository;

    public DigitalCustomerDeviceServiceImpl(DigitalCustomerDeviceRepository digitalCustomerDeviceRepository) {
        this.digitalCustomerDeviceRepository = digitalCustomerDeviceRepository;
    }

    @Override
    public Mono<DigitalCustomerDeviceResponse> getDeviceInfo(String deviceId) {
        log.debug("Fetching device information for deviceId: " + deviceId);
        return Mono.justOrEmpty(digitalCustomerDeviceRepository.findByDigitalDeviceUdid(deviceId))
                .map(this::mapToDigitalCustomerDeviceResponse);
    }

    DigitalCustomerDeviceResponse mapToDigitalCustomerDeviceResponse(DigitalCustomerDevice
                                                                             digitalCustomerDevice) {
        log.debug("Mapping DigitalCustomerDevice to DigitalCustomerDeviceResponse");
        return DigitalCustomerDeviceResponse.builder()
                .digitalDeviceUdid(digitalCustomerDevice.getDigitalDeviceUdid())
                .digitalCustomerDeviceId(digitalCustomerDevice.getDigitalCustomerDeviceId())
                .deviceName(digitalCustomerDevice.getDeviceName())
                .deviceType(digitalCustomerDevice.getDeviceType())
                .deviceOsVersion(digitalCustomerDevice.getDeviceOsVersion())
                .build();
    }

    @Override
    public BiometricStatusResponse getBiometricStatusForDevice(
            UUID digitalCustomerProfileId, String digitalDeviceUDID) {
        log.debug("Inside getBiometricStatusForDevice() to get customer biometric status : {}",
                digitalCustomerProfileId);

        Object[] result = digitalCustomerDeviceRepository.getBiometricStatusForDevice(
                digitalCustomerProfileId, digitalDeviceUDID);

        if (result == null || result.length == 0) {
            throw new DigitalCustomerDeviceNotFoundException(
                    "Biometric status not found for the given digital device");
        }

        Object[] innerResult = (Object[]) result[0];

        if (innerResult == null || innerResult.length != 2) {
            throw new DigitalCustomerDeviceNotFoundException("Unexpected result structure");
        }

        BiometricStatusResponse response = new BiometricStatusResponse();
        response.setFaceId(Boolean.parseBoolean(innerResult[0].toString()));
        response.setTouchId(Boolean.parseBoolean(innerResult[1].toString()));

        return response;
    }

    @Override
    public DeviceRegistrationResponseDTO checkDeviceRegistration(UUID digitalCustomerProfileID,
                                                                 String digitalDeviceUUID) {

        if (StringUtils.isBlank(digitalDeviceUUID)) {
            throw new IllegalArgumentException("Digital Device UUID cannot be null");
        }

        Boolean isRegistered = digitalCustomerDeviceRepository.isDeviceRegisteredForDigitalCustomerProfile(
                digitalCustomerProfileID, digitalDeviceUUID);

        if (Boolean.TRUE.equals(isRegistered)) {
            return DeviceRegistrationResponseDTO.builder().isRegistered(true).build();
        } else {
            CustomerContactInfo contactInfo = digitalCustomerDeviceRepository
                    .findEmailAndMobileNumberByDigitalCustomerProfileId(digitalCustomerProfileID);
            if (contactInfo == null) {
                throw new CustomerContactNotFoundException("Customer contact information not found");
            }
            return new DeviceRegistrationResponseDTO(false, contactInfo.getCustomerEmail(),
                    contactInfo.getCustomerMobileNo());
        }
    }
}