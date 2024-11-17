package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.dto.response.BiometricStatusResponse;
import com.unisys.udb.user.dto.response.DeviceRegistrationResponseDTO;
import com.unisys.udb.user.entity.DigitalCustomerDevice;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import com.unisys.udb.user.entity.DigitalDeviceLink;
import com.unisys.udb.user.exception.CustomerContactNotFoundException;
import com.unisys.udb.user.exception.DigitalCustomerDeviceNotFoundException;
import com.unisys.udb.user.repository.CustomerContactInfo;
import com.unisys.udb.user.repository.DigitalCustomerDeviceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {DigitalCustomerDeviceServiceImpl.class})
@ExtendWith(SpringExtension.class)
class DigitalCustomerDeviceServiceImplTest {
    @MockBean
    private DigitalCustomerDeviceRepository digitalCustomerDeviceRepository;

    @Autowired
    private DigitalCustomerDeviceServiceImpl digitalCustomerDeviceServiceImpl;

    /**
     * Method under test:
     * {@link DigitalCustomerDeviceServiceImpl#getDeviceInfo(String)}
     */

    @Test
    void testGetDeviceInfo() {
        DigitalDeviceLink digitalDeviceLink = new DigitalDeviceLink();
        digitalDeviceLink.setDeviceLinkCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalDeviceLink.setDeviceLinkCreationDate(LocalDate.now().atStartOfDay());
        digitalDeviceLink.setDeviceLinkModificationDate(LocalDate.now().atStartOfDay());
        digitalDeviceLink.setDeviceLinkModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalDeviceLink.setDeviceLinkRegisterFlag(true);
        digitalDeviceLink.setDigitalCustomerDevice(new DigitalCustomerDevice());
        digitalDeviceLink.setDigitalCustomerDeviceAudit(new ArrayList<>());
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
        digitalDeviceLink2.setDeviceLinkRegisterFlag(true);
        digitalDeviceLink2.setDigitalCustomerDevice(new DigitalCustomerDevice());
        digitalDeviceLink2.setDigitalCustomerDeviceAudit(new ArrayList<>());
        digitalDeviceLink2.setDigitalCustomerProfile(new DigitalCustomerProfile());
        digitalDeviceLink2.setDigitalDeviceLinkId(1);

        DigitalCustomerProfile digitalCustomerProfile = new DigitalCustomerProfile();
        digitalCustomerProfile.setCoreCustomerProfileId(UUID.randomUUID());
        digitalCustomerProfile.setDigitalAccountStatusReason("Just cause");
        digitalCustomerProfile.setDigitalCustomerProfileId(UUID.randomUUID());
        digitalCustomerProfile.setDigitalCustomerStatusTypeId(1);

        List<DigitalDeviceLink> digitalDeviceLinks = new ArrayList<>();
        digitalDeviceLinks.add(digitalDeviceLink2);
        digitalCustomerProfile.setDigitalDeviceLink(digitalDeviceLinks);

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
        digitalDeviceLink3.setDeviceLinkRegisterFlag(true);
        digitalDeviceLink3.setDigitalCustomerDevice(digitalCustomerDevice);
        digitalDeviceLink3.setDigitalCustomerDeviceAudit(new ArrayList<>());
        digitalDeviceLink3.setDigitalCustomerProfile(digitalCustomerProfile);
        digitalDeviceLink3.setDigitalDeviceLinkId(1);

        DigitalCustomerDevice digitalCustomerDevice2 = new DigitalCustomerDevice();
        digitalCustomerDevice2.setDeviceCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerDevice2.setDeviceCreationDate(LocalDate.now().atStartOfDay());
        digitalCustomerDevice2.setDeviceFacePublicKey("Device Face Public Key");
        digitalCustomerDevice2.setDeviceModificationDate(LocalDate.now().atStartOfDay());
        digitalCustomerDevice2.setDeviceModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCustomerDevice2.setDeviceName("Device Name");
        digitalCustomerDevice2.setDeviceOsVersion("1.0.2");
        digitalCustomerDevice2.setDevicePinPublicKey("Device Pin Public Key");
        digitalCustomerDevice2.setDeviceStatus(true);
        digitalCustomerDevice2.setDeviceToken("ABC123");
        digitalCustomerDevice2.setDeviceType("Device Type");
        digitalCustomerDevice2.setDigitalCustomerDeviceId(1);
        digitalCustomerDevice2.setDigitalDeviceLink(digitalDeviceLink3);
        digitalCustomerDevice2.setDigitalDeviceUdid("Digital Device Udid");
        digitalCustomerDevice2.setFunctionalCookie(true);
        digitalCustomerDevice2.setPerformanceCookie(true);
        digitalCustomerDevice2.setStrictlyAcceptanceCookie(true);
        digitalCustomerDevice2.setTermsAndConditions(true);

        Optional<DigitalCustomerDevice> ofResult = Optional.of(digitalCustomerDevice2);
        when(digitalCustomerDeviceRepository.findByDigitalDeviceUdid(Mockito.<String>any())).thenReturn(ofResult);
        digitalCustomerDeviceServiceImpl.getDeviceInfo("42");
        verify(digitalCustomerDeviceRepository).findByDigitalDeviceUdid(Mockito.<String>any());
    }


    @Test
    void testGetBiometricStatusForDevice() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        String digitalDeviceUDID = "testDeviceUDID";

        Object[] mockResult = new Object[]{true, false};
        when(digitalCustomerDeviceRepository.getBiometricStatusForDevice(any(UUID.class), any(String.class)))
                .thenReturn(new Object[]{mockResult});

        // Call the method and assert the response
        BiometricStatusResponse response =
                digitalCustomerDeviceServiceImpl.getBiometricStatusForDevice(digitalCustomerProfileId,
                        digitalDeviceUDID);

        Assertions.assertTrue(response.isFaceId());
        Assertions.assertFalse(response.isTouchId());
    }

    @Test
    void testGetBiometricStatusDeviceNotFound() {
        UUID profileId = UUID.randomUUID();
        String deviceUdid = "12345";

        Object[] result = digitalCustomerDeviceRepository.getBiometricStatusForDevice(profileId, deviceUdid);
        when(result).thenReturn(null);

        if (result == null || result.length == 0) {
            Assertions.assertThrows(DigitalCustomerDeviceNotFoundException.class, () -> {
                digitalCustomerDeviceServiceImpl.getBiometricStatusForDevice(profileId, deviceUdid);
            });
        }

    }

    @Test
    void testGetBiometricStatusForDeviceUnexpectedResultStructure() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        String digitalDeviceUDID = "testDeviceUDID";

        when(digitalCustomerDeviceRepository.getBiometricStatusForDevice(any(UUID.class), any(String.class)))
                .thenReturn(new Object[]{new Object[]{true}});

        assertThrows(DigitalCustomerDeviceNotFoundException.class, () ->
                digitalCustomerDeviceServiceImpl.getBiometricStatusForDevice(digitalCustomerProfileId,
                        digitalDeviceUDID));
    }

    @Test
    void testDeviceRegistered() {
        UUID customerId = UUID.randomUUID();
        String deviceUUID = "test-uuid";
        when(digitalCustomerDeviceRepository.isDeviceRegisteredForDigitalCustomerProfile(customerId, deviceUUID))
                .thenReturn(true);

        DeviceRegistrationResponseDTO response = digitalCustomerDeviceServiceImpl.checkDeviceRegistration(
        customerId, deviceUUID);

        Assertions.assertTrue(response.getIsRegistered());
        Assertions.assertNull(response.getEmail());
        Assertions.assertNull(response.getMobileNumber());
    }

    @Test
    void testDeviceNotRegisteredWithContactInfo() {
        UUID customerId = UUID.randomUUID();
        String deviceUUID = "test-uuid";
        CustomerContactInfo contactInfo = new CustomerContactInfo() {
            @Override
            public String getCustomerEmail() {
                return "g@gmail.com";
            }

            @Override
            public String getCustomerMobileNo() {
                return "1234567890";
            }
        };
        when(digitalCustomerDeviceRepository.isDeviceRegisteredForDigitalCustomerProfile(customerId, deviceUUID))
                .thenReturn(false);
        when(digitalCustomerDeviceRepository.findEmailAndMobileNumberByDigitalCustomerProfileId(customerId))
                .thenReturn(contactInfo);

        DeviceRegistrationResponseDTO response = digitalCustomerDeviceServiceImpl.checkDeviceRegistration(
                customerId, deviceUUID);

        Assertions.assertFalse(response.getIsRegistered());
        Assertions.assertEquals("g@gmail.com", response.getEmail());
        Assertions.assertEquals("1234567890", response.getMobileNumber());
    }

    @Test
    void testDeviceNotRegisteredWithoutContactInfo() {
        UUID customerId = UUID.randomUUID();
        String deviceUUID = "test-uuid";
        when(digitalCustomerDeviceRepository.isDeviceRegisteredForDigitalCustomerProfile(customerId, deviceUUID))
                .thenReturn(false);
        when(digitalCustomerDeviceRepository.findEmailAndMobileNumberByDigitalCustomerProfileId(customerId))
                .thenReturn(null);

        assertThrows(CustomerContactNotFoundException.class, () -> {
            digitalCustomerDeviceServiceImpl.checkDeviceRegistration(customerId, deviceUUID);
        });
    }

    @Test
    void testNullDeviceUUID() {
        UUID customerId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> {
            digitalCustomerDeviceServiceImpl.checkDeviceRegistration(customerId, null);
        });
    }

}
