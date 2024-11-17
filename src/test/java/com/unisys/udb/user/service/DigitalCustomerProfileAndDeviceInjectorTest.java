package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.request.DigitalCustomerProfileDTO;
import com.unisys.udb.user.entity.DigitalCustomerDevice;
import com.unisys.udb.user.entity.DigitalCustomerDeviceAudit;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import com.unisys.udb.user.entity.DigitalDeviceLink;
import com.unisys.udb.user.repository.*;
import com.unisys.udb.user.service.impl.UserInfoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {DigitalCustomerProfileAndDeviceInjector.class})
@ExtendWith(SpringExtension.class)
class DigitalCustomerProfileAndDeviceInjectorTest {


    @MockBean
    private UserInfoServiceImpl userInfoServiceImpl;


    @MockBean
    private DigitalCustomerDeviceRepository digitalCustomerDeviceRepository;

    @Autowired
    private DigitalCustomerProfileAndDeviceInjector digitalCustomerProfileAndDeviceInjector;

    @MockBean
    private DigitalCustomerProfileRepository digitalCustomerProfileRepository;

    @MockBean
    private DigitalDeviceLinkRepository digitalDeviceLinkRepository;
    @MockBean
    private DigitalCookiePreferenceRepository digitalCookiePreferenceRepository;
    @MockBean
    private DigitalCustomerDeviceAuditRepository digitalCustomerDeviceAuditRepository;



    @Test
    void testInsertDigitalProfileDeviceLink() {
        // Create a DigitalCustomerProfileDTO for testing
        DigitalCustomerProfileDTO dto = new DigitalCustomerProfileDTO();
        dto.setDigitalCustomerProfileId(UUID.randomUUID().toString());
        dto.setCoreCustomerProfileId(UUID.randomUUID().toString());
        dto.setDigitalUserName("testUser");
        dto.setDigitalDeviceUdid("123456");
        dto.setDeviceName("TestDevice");
        dto.setDeviceType("Mobile");
        dto.setDeviceOsVersion("1.0");
        List<Object[]> resultList = new ArrayList<>();
        Object[] cookieAttributes = new Object[]{true, true, true};
        resultList.add(cookieAttributes);

        DigitalCustomerDevice digitalCustomerDevice = new DigitalCustomerDevice();
        digitalCustomerDevice.setDigitalDeviceUdid("testDeviceUdid");
        digitalCustomerDevice.setDeviceName("testDeviceName");
        digitalCustomerDevice.setDeviceType("testDeviceType");
        digitalCustomerDevice.setDeviceOsVersion("testOsVersion");
        digitalCustomerDevice.setDeviceToken("testDeviceToken");
        digitalCustomerDevice.setDeviceStatus(true);
        digitalCustomerDevice.setDeviceCreationDate(LocalDateTime.now());
        digitalCustomerDevice.setDeviceCreatedBy("testCreator");
        digitalCustomerDevice.setDeviceModificationDate(LocalDateTime.now());
        digitalCustomerDevice.setDeviceModifiedBy("testModifier");
        digitalCustomerDevice.setTermsAndConditions(true);
        digitalCustomerDevice.setStrictlyAcceptanceCookie(true);
        digitalCustomerDevice.setPerformanceCookie(true);
        digitalCustomerDevice.setFunctionalCookie(true);
        digitalCustomerDevice.setDeviceFacePublicKey("testFacePublicKey");
        digitalCustomerDevice.setDeviceTouchPublicKey("testTouchPublicKey");
        digitalCustomerDevice.setDevicePinPublicKey("testPinPublicKey");

        // Assuming DigitalDeviceLink has a parameterless constructor
        digitalCustomerDevice.setDigitalDeviceLink(new DigitalDeviceLink());


//        doReturn(digitalCustomerProfile).when(digitalCustomerProfileRepository).findByDigitalCustomerProfileId(
//        Mockito.any());
        doReturn(resultList).when(digitalCustomerDeviceRepository).retrieveCookiePreferenceByDeviceUdid(Mockito.any());

        when(digitalCustomerDeviceRepository.findByDigitalDeviceUdid(any())).thenReturn(Optional.of(
                digitalCustomerDevice));
        // Call the method to be tested
        digitalCustomerProfileAndDeviceInjector.insertDigitalProfileDeviceLink(dto);

        // Verify that saveAndFlush is called on each repository with the correct entities
        verify(digitalCustomerProfileRepository, times(1)).saveAndFlush(any(
                DigitalCustomerProfile.class));
        verify(digitalCustomerDeviceRepository, times(1)).saveAndFlush(
                any(DigitalCustomerDevice.class));
        verify(digitalDeviceLinkRepository, times(1)).saveAndFlush(
                any(DigitalDeviceLink.class));
        verify(digitalCustomerDeviceAuditRepository, times(1)).insertData(
                any(DigitalCustomerDeviceAudit.class));
    }
}

