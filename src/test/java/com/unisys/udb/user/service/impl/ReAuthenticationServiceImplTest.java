package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.dto.request.ReAuthenticateActivityRequest;
import com.unisys.udb.user.entity.DigitalActivityDetailRef;
import com.unisys.udb.user.entity.DigitalCustomerActivityEntity;
import com.unisys.udb.user.entity.DigitalCustomerDevice;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import com.unisys.udb.user.entity.DigitalDeviceLink;
import com.unisys.udb.user.repository.DigitalActivityDetailRefRepository;
import com.unisys.udb.user.repository.DigitalCustomerActivityRepository;
import com.unisys.udb.user.repository.DigitalCustomerDeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ReAuthenticationServiceImpl.class})
@ExtendWith(SpringExtension.class)
class ReAuthenticationServiceImplTest {
    private static final int BASE_YEAR = 1970;
    private static final int DEVICE_ID = 42;

    @MockBean
    private DigitalActivityDetailRefRepository digitalActivityDetailRefRepository;
    @MockBean
    private DigitalCustomerActivityRepository digitalCustomerActivityRepository;
    @MockBean
    private DigitalCustomerDeviceRepository digitalCustomerDeviceRepository;
    @Autowired
    private ReAuthenticationServiceImpl reAuthenticationServiceImpl;

    /**
     * Method under test:
     * {@link ReAuthenticationServiceImpl#addReAuthenticateActivity(
     * ReAuthenticateActivityRequest)}
     */
    @Test
    void testAddReAuthenticateActivity() {
        DigitalDeviceLink digitalDeviceLink = new DigitalDeviceLink();
        digitalDeviceLink.setDeviceLinkCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalDeviceLink.setDeviceLinkCreationDate(LocalDate.of(BASE_YEAR, 1, 1).atStartOfDay());
        digitalDeviceLink.setDeviceLinkModificationDate(LocalDate.of(BASE_YEAR, 1, 1).atStartOfDay());
        digitalDeviceLink.setDeviceLinkModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalDeviceLink.setDeviceLinkRegisterFlag(true);
        digitalDeviceLink.setDigitalCustomerDevice(new DigitalCustomerDevice());
        digitalDeviceLink.setDigitalCustomerDeviceAudit(new ArrayList<>());
        digitalDeviceLink.setDigitalCustomerProfile(new DigitalCustomerProfile());
        digitalDeviceLink.setDigitalDeviceLinkId(1);

        DigitalCustomerDevice digitalCustomerDevice = new DigitalCustomerDevice();
        digitalCustomerDevice.setDeviceCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerDevice.setDeviceCreationDate(LocalDate.of(BASE_YEAR, 1, 1).atStartOfDay());
        digitalCustomerDevice.setDeviceFacePublicKey("Device Face Public Key");
        digitalCustomerDevice.setDeviceModificationDate(LocalDate.of(BASE_YEAR, 1, 1).atStartOfDay());
        digitalCustomerDevice.setDeviceModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCustomerDevice.setDeviceName("Device Name");
        digitalCustomerDevice.setDeviceOsVersion("1.0.2");
        digitalCustomerDevice.setDevicePinPublicKey("Device Pin Public Key");
        digitalCustomerDevice.setDeviceStatus(true);
        digitalCustomerDevice.setDeviceToken("ABC123");
        digitalCustomerDevice.setDeviceTouchPublicKey("Device Touch Public Key");
        digitalCustomerDevice.setDeviceType("Device Type");
        digitalCustomerDevice.setDigitalCustomerDeviceId(DEVICE_ID);
        digitalCustomerDevice.setDigitalDeviceLink(digitalDeviceLink);
        digitalCustomerDevice.setDigitalDeviceUdid("Digital Device Udid");
        digitalCustomerDevice.setFunctionalCookie(true);
        digitalCustomerDevice.setPerformanceCookie(true);
        digitalCustomerDevice.setStrictlyAcceptanceCookie(true);
        digitalCustomerDevice.setTermsAndConditions(true);

        DigitalCustomerProfile digitalCustomerProfile = new DigitalCustomerProfile();
        digitalCustomerProfile.setCoreCustomerProfileId(UUID.randomUUID());
        digitalCustomerProfile.setDigitalAccountStatusReason("Just cause");
        digitalCustomerProfile.setDigitalCustomerProfileId(UUID.randomUUID());
        digitalCustomerProfile.setDigitalCustomerStatusTypeId(1);
        digitalCustomerProfile.setDigitalDeviceLink(new ArrayList<>());
        digitalCustomerProfile.setDigitalUserName("janedoe");
        digitalCustomerProfile.setMfaActivityCompleted(true);
        digitalCustomerProfile.setPinExpiryDate(LocalDate.of(BASE_YEAR, 1, 1).atStartOfDay());
        digitalCustomerProfile.setPinSetCompleted(true);
        digitalCustomerProfile.setProfileCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerProfile.setProfileModificationDate(
                LocalDate.of(BASE_YEAR, 1, 1).atStartOfDay());
        digitalCustomerProfile.setProfileModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCustomerProfile.setPwdExpiryDate(LocalDate.of(BASE_YEAR, 1, 1).atStartOfDay());
        digitalCustomerProfile.setRegistrationDate(LocalDate.of(BASE_YEAR, 1, 1).atStartOfDay());

        DigitalDeviceLink digitalDeviceLink2 = new DigitalDeviceLink();
        digitalDeviceLink2.setDeviceLinkCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalDeviceLink2.setDeviceLinkCreationDate(LocalDate.of(BASE_YEAR, 1, 1).atStartOfDay());
        digitalDeviceLink2.setDeviceLinkModificationDate(LocalDate.of(BASE_YEAR, 1, 1).atStartOfDay());
        digitalDeviceLink2.setDeviceLinkModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalDeviceLink2.setDeviceLinkRegisterFlag(true);
        digitalDeviceLink2.setDigitalCustomerDevice(digitalCustomerDevice);
        digitalDeviceLink2.setDigitalCustomerDeviceAudit(new ArrayList<>());
        digitalDeviceLink2.setDigitalCustomerProfile(digitalCustomerProfile);
        digitalDeviceLink2.setDigitalDeviceLinkId(1);

        DigitalCustomerDevice digitalCustomerDevice2 = new DigitalCustomerDevice();
        digitalCustomerDevice2.setDeviceCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerDevice2.setDeviceCreationDate(LocalDate.of(BASE_YEAR, 1, 1).atStartOfDay());
        digitalCustomerDevice2.setDeviceFacePublicKey("Device Face Public Key");
        digitalCustomerDevice2.setDeviceModificationDate(LocalDate.of(BASE_YEAR, 1, 1).atStartOfDay());
        digitalCustomerDevice2.setDeviceModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCustomerDevice2.setDeviceName("Device Name");
        digitalCustomerDevice2.setDeviceOsVersion("1.0.2");
        digitalCustomerDevice2.setDevicePinPublicKey("Device Pin Public Key");
        digitalCustomerDevice2.setDeviceStatus(true);
        digitalCustomerDevice2.setDeviceToken("ABC123");
        digitalCustomerDevice2.setDeviceTouchPublicKey("Device Touch Public Key");
        digitalCustomerDevice2.setDeviceType("Device Type");
        digitalCustomerDevice2.setDigitalCustomerDeviceId(DEVICE_ID);
        digitalCustomerDevice2.setDigitalDeviceLink(digitalDeviceLink2);
        digitalCustomerDevice2.setDigitalDeviceUdid("Digital Device Udid");
        digitalCustomerDevice2.setFunctionalCookie(true);
        digitalCustomerDevice2.setPerformanceCookie(true);
        digitalCustomerDevice2.setStrictlyAcceptanceCookie(true);
        digitalCustomerDevice2.setTermsAndConditions(true);
        Optional<DigitalCustomerDevice> ofResult = Optional.of(digitalCustomerDevice2);
        when(digitalCustomerDeviceRepository.findByDigitalDeviceUdid(Mockito.<String>any())).thenReturn(ofResult);

        DigitalActivityDetailRef digitalActivityDetailRef = new DigitalActivityDetailRef();
        digitalActivityDetailRef.setActivityCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalActivityDetailRef.setActivityCreationDate(LocalDate.of(BASE_YEAR, 1, 1).atStartOfDay());
        digitalActivityDetailRef.setActivityModificationDate(
                LocalDate.of(BASE_YEAR, 1, 1).atStartOfDay());
        digitalActivityDetailRef.setActivityModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalActivityDetailRef.setDigitalActivityDetailRefId(1);
        digitalActivityDetailRef.setDigitalActivityName("Digital Activity Name");
        when(digitalActivityDetailRefRepository.findByDigitalActivityName(Mockito.<String>any()))
                .thenReturn(digitalActivityDetailRef);

        DigitalCustomerActivityEntity digitalCustomerActivityEntity = new DigitalCustomerActivityEntity();
        digitalCustomerActivityEntity.setActivityChannel("Activity Channel");
        digitalCustomerActivityEntity.setActivityCorelationKey(UUID.randomUUID());
        digitalCustomerActivityEntity.setActivityStatus("Activity Status");
        digitalCustomerActivityEntity.setActivityTime(LocalDate.of(BASE_YEAR, 1, 1).atStartOfDay());
        digitalCustomerActivityEntity.setDigitalActivityDetailRefId(1);
        digitalCustomerActivityEntity.setDigitalCustomerActivityId(1);
        digitalCustomerActivityEntity.setDigitalCustomerDeviceId(1);
        digitalCustomerActivityEntity.setDigitalCustomerProfileId(UUID.randomUUID());

        DigitalCustomerActivityEntity digitalCustomerActivityEntity2 = new DigitalCustomerActivityEntity();
        digitalCustomerActivityEntity2.setActivityChannel("Activity Channel");
        digitalCustomerActivityEntity2.setActivityCorelationKey(UUID.randomUUID());
        digitalCustomerActivityEntity2.setActivityStatus("Activity Status");
        digitalCustomerActivityEntity2.setActivityTime(LocalDate.of(BASE_YEAR, 1, 1).atStartOfDay());
        digitalCustomerActivityEntity2.setDigitalActivityDetailRefId(1);
        digitalCustomerActivityEntity2.setDigitalCustomerActivityId(1);
        digitalCustomerActivityEntity2.setDigitalCustomerDeviceId(1);
        digitalCustomerActivityEntity2.setDigitalCustomerProfileId(UUID.randomUUID());
        when(digitalCustomerActivityRepository.save(Mockito.<DigitalCustomerActivityEntity>any()))
                .thenReturn(digitalCustomerActivityEntity2);
        when(digitalCustomerActivityRepository.findTopByDigitalCustomerDeviceIdOrderByActivityTimeDesc(anyInt()))
                .thenReturn(digitalCustomerActivityEntity);

        ReAuthenticateActivityRequest reAuthenticateActivityRequest = new ReAuthenticateActivityRequest();
        reAuthenticateActivityRequest.setChannel("Channel");
        reAuthenticateActivityRequest.setDigitalDeviceUdid("Digital Device Udid");
        reAuthenticateActivityRequest.setStatus("Status");
        String actualAddReAuthenticateActivityResult = reAuthenticationServiceImpl
                .addReAuthenticateActivity(reAuthenticateActivityRequest);
        verify(digitalActivityDetailRefRepository).findByDigitalActivityName(Mockito.<String>any());
        verify(digitalCustomerActivityRepository).findTopByDigitalCustomerDeviceIdOrderByActivityTimeDesc(anyInt());
        verify(digitalCustomerDeviceRepository).findByDigitalDeviceUdid(Mockito.<String>any());
        verify(digitalCustomerActivityRepository).save(Mockito.<DigitalCustomerActivityEntity>any());
        assertEquals("Re-Authentication activity recorded successfully", actualAddReAuthenticateActivityResult);
    }
}