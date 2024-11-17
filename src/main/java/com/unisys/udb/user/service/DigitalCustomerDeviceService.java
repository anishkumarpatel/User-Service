package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.response.BiometricStatusResponse;
import com.unisys.udb.user.dto.response.DeviceRegistrationResponseDTO;
import com.unisys.udb.user.dto.response.DigitalCustomerDeviceResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DigitalCustomerDeviceService {
    Mono<DigitalCustomerDeviceResponse> getDeviceInfo(String deviceId);
    BiometricStatusResponse getBiometricStatusForDevice(UUID digitalCustomerProfileId, String digitalDeviceUDID);

    DeviceRegistrationResponseDTO checkDeviceRegistration(UUID digitalCustomerProfileID, String digitalDeviceUUID);

}
