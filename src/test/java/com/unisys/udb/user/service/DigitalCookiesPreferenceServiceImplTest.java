package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.request.DigitalCookiesPreferenceRequest;
import com.unisys.udb.user.dto.response.DigitalCookiesPreferenceResponse;
import com.unisys.udb.user.entity.DigitalCookiePreference;
import com.unisys.udb.user.exception.DigitalCustomerProfileIdNotFoundException;
import com.unisys.udb.user.repository.DigitalCookiePreferenceRepository;
import com.unisys.udb.user.repository.DigitalCustomerProfileRepository;
import com.unisys.udb.user.repository.UserInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.unisys.udb.user.constants.UdbConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {DigitalCookiesPreferenceServiceImpl.class})
@ExtendWith(SpringExtension.class)
class DigitalCookiesPreferenceServiceImplTest {
    private static final int YEAR_1970 = 1970;

    @MockBean
    private DigitalCookiePreferenceRepository digitalCookiePreferenceRepository;

    @Autowired
    private DigitalCookiesPreferenceServiceImpl digitalCookiesPreferenceServiceImpl;

    @MockBean
    private DigitalCustomerProfileRepository digitalCustomerProfileRepository;
    @MockBean
    private UserInfoRepository userInfoRepository;

    @Test
    void testSaveDigitalCookiesPreferences() {
        when(digitalCustomerProfileRepository.existsByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn(false);
        UUID digitalCustomerProfileId = UUID.randomUUID();
        DigitalCookiesPreferenceRequest cookiesPreferenceRequest = new DigitalCookiesPreferenceRequest();
        cookiesPreferenceRequest.setFunctionalCookie(false);
        assertThrows(DigitalCustomerProfileIdNotFoundException.class, () -> digitalCookiesPreferenceServiceImpl
                .saveDigitalCookiesPreferences(digitalCustomerProfileId, cookiesPreferenceRequest));
        verify(digitalCustomerProfileRepository).existsByDigitalCustomerProfileId(Mockito.<UUID>any());
    }

    @Test
    void testSaveDigitalCookiesPreferences2() {
        DigitalCookiePreference digitalCookiePreference = new DigitalCookiePreference();
        digitalCookiePreference.setCookieCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCookiePreference.setCookieCreationDate(LocalDate.of(YEAR_1970, 1, 1).atStartOfDay());
        digitalCookiePreference.setCookieModificationDate(LocalDate.of(YEAR_1970, 1, 1).atStartOfDay());
        digitalCookiePreference.setCookieModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCookiePreference.setDigitalCookiePreferenceId(1);
        digitalCookiePreference.setDigitalCustomerProfileId(UUID.fromString("06038CDB-3A09-4BA4-B767-DC52F95E3091"));
        digitalCookiePreference.setFunctionalCookie(true);
        digitalCookiePreference.setPerformanceCookie(true);
        digitalCookiePreference.setStrictlyAcceptanceCookie(true);
        DigitalCookiePreference preference = new DigitalCookiePreference();
        preference.setDigitalCustomerProfileId(UUID.fromString("06038CDB-3A09-4BA4-B767-DC52F95E3091"));
        Optional<DigitalCookiePreference> optional = Optional.of(preference);
        when(digitalCookiePreferenceRepository.save(Mockito.<DigitalCookiePreference>any()))
                .thenReturn(digitalCookiePreference);
        when(digitalCustomerProfileRepository.existsByDigitalCustomerProfileId(
                Mockito.<UUID>any())).thenReturn(true);
        when(digitalCookiePreferenceRepository.findByDigitalCustomerProfileId(UUID.fromString(
                "06038CDB-3A09-4BA4-B767-DC52F95E3091")))
                .thenReturn(optional);
        UUID digitalCustomerProfileId = UUID.fromString("06038CDB-3A09-4BA4-B767-DC52F95E3091");

        DigitalCookiesPreferenceRequest cookiesPreferenceRequest = new DigitalCookiesPreferenceRequest(
                true, true, true);
        cookiesPreferenceRequest.setFunctionalCookie(false);
        Mono<DigitalCookiesPreferenceResponse> actualResponse = digitalCookiesPreferenceServiceImpl
                .saveDigitalCookiesPreferences(digitalCustomerProfileId,
                        cookiesPreferenceRequest);
        assertEquals("Digital Cookies Preference Data Updated successfully",
                actualResponse.block().getMessage());
    }

    @Test
    void testSaveDigitalCookiesPreferences4() {
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        when(digitalCookiePreferenceRepository.findByDigitalCustomerProfileId(Mockito.<UUID>any()))
                .thenThrow(new DigitalCustomerProfileIdNotFoundException(errorCode,
                        HttpStatus.NOT_FOUND,
                        FAILURE,
                        NOT_FOUND_ERROR_MESSAGE + UUID.randomUUID(),
                        new ArrayList<>()));
        when(digitalCustomerProfileRepository.existsByDigitalCustomerProfileId(Mockito.<UUID>any()))
                .thenReturn(true);
        UUID digitalCustomerProfileId = UUID.randomUUID();
        DigitalCookiesPreferenceRequest digitalCookiesPreferenceRequest = new DigitalCookiesPreferenceRequest();
        assertThrows(DigitalCustomerProfileIdNotFoundException.class, () -> digitalCookiesPreferenceServiceImpl
                .saveDigitalCookiesPreferences(digitalCustomerProfileId, digitalCookiesPreferenceRequest));
        verify(digitalCookiePreferenceRepository).findByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(digitalCustomerProfileRepository).existsByDigitalCustomerProfileId(Mockito.<UUID>any());
    }

    @Test
    void testSaveDigitalCookiesPreferences5() {
        Optional<DigitalCookiePreference> emptyResult = Optional.empty();
        when(digitalCookiePreferenceRepository.findByDigitalCustomerProfileId(
                Mockito.<UUID>any())).thenReturn(emptyResult);
        when(digitalCustomerProfileRepository.existsByDigitalCustomerProfileId(
                Mockito.<UUID>any())).thenReturn(true);
        UUID digitalCustomerProfileId = UUID.randomUUID();
        DigitalCookiesPreferenceRequest digitalCookiesPreferenceRequest = new DigitalCookiesPreferenceRequest();
        assertThrows(DigitalCustomerProfileIdNotFoundException.class, () -> digitalCookiesPreferenceServiceImpl
                .saveDigitalCookiesPreferences(digitalCustomerProfileId, digitalCookiesPreferenceRequest));
        verify(digitalCookiePreferenceRepository).findByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(digitalCustomerProfileRepository).existsByDigitalCustomerProfileId(Mockito.<UUID>any());
    }


    @Test
    void testSaveDigitalCookiesPreferences6() {
        DigitalCookiePreference digitalCookiePreference = new DigitalCookiePreference();
        digitalCookiePreference.setCookieCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCookiePreference.setCookieCreationDate(LocalDate.of(YEAR_1970, 1, 1).atStartOfDay());
        digitalCookiePreference.setCookieModificationDate(LocalDate.of(
                YEAR_1970, 1, 1).atStartOfDay());
        digitalCookiePreference.setCookieModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCookiePreference.setDigitalCookiePreferenceId(1);
        digitalCookiePreference.setDigitalCustomerProfileId(UUID.randomUUID());
        digitalCookiePreference.setFunctionalCookie(true);
        digitalCookiePreference.setPerformanceCookie(true);
        digitalCookiePreference.setStrictlyAcceptanceCookie(true);
        Optional<DigitalCookiePreference> ofResult = Optional.of(digitalCookiePreference);

        DigitalCookiePreference digitalCookiePreference2 = new DigitalCookiePreference();
        digitalCookiePreference2.setCookieCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCookiePreference2.setCookieCreationDate(LocalDate.of(
                YEAR_1970, 1, 1).atStartOfDay());
        digitalCookiePreference2.setCookieModificationDate(LocalDate.of(
                YEAR_1970, 1, 1).atStartOfDay());
        digitalCookiePreference2.setCookieModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        digitalCookiePreference2.setDigitalCookiePreferenceId(1);
        digitalCookiePreference2.setDigitalCustomerProfileId(UUID.randomUUID());
        digitalCookiePreference2.setFunctionalCookie(true);
        digitalCookiePreference2.setPerformanceCookie(true);
        digitalCookiePreference2.setStrictlyAcceptanceCookie(true);
        when(digitalCookiePreferenceRepository.save(Mockito.<DigitalCookiePreference>any()))
                .thenReturn(digitalCookiePreference2);
        when(digitalCookiePreferenceRepository.findByDigitalCustomerProfileId(Mockito.<UUID>any()))
                .thenReturn(ofResult);
        when(digitalCustomerProfileRepository.existsByDigitalCustomerProfileId(
                Mockito.<UUID>any())).thenReturn(true);
        UUID digitalCustomerProfileId = UUID.randomUUID();

        DigitalCookiesPreferenceRequest cookiesPreferenceRequest = new DigitalCookiesPreferenceRequest(
                true, true, true);
        cookiesPreferenceRequest.setFunctionalCookie(false);
        digitalCookiesPreferenceServiceImpl.saveDigitalCookiesPreferences(digitalCustomerProfileId,
                cookiesPreferenceRequest);
        verify(digitalCookiePreferenceRepository).findByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(digitalCustomerProfileRepository).existsByDigitalCustomerProfileId(Mockito.<UUID>any());
        verify(digitalCookiePreferenceRepository).save(Mockito.<DigitalCookiePreference>any());
    }

    @Test
    void testSetDigitalCookiesPreference() {
        UUID digitalCustomerProfileId = UUID.randomUUID();

        DigitalCookiesPreferenceRequest cookiesPreferenceRequest = new DigitalCookiesPreferenceRequest(
                true, true, true);
        cookiesPreferenceRequest.setFunctionalCookie(false);
        DigitalCookiePreference digitalCookiePreference1 = new DigitalCookiePreference();
        DigitalCookiePreference actualSetDigitalCookiesPreferenceResult = digitalCookiesPreferenceServiceImpl
                .setDigitalCookiesPreference(digitalCustomerProfileId,
                        cookiesPreferenceRequest, digitalCookiePreference1);
        assertEquals(userInfoRepository.findUserNameByDigitalCustomerProfileId(digitalCustomerProfileId),
                actualSetDigitalCookiesPreferenceResult.getCookieCreatedBy());
        assertEquals(userInfoRepository.findUserNameByDigitalCustomerProfileId(digitalCustomerProfileId),
                actualSetDigitalCookiesPreferenceResult.getCookieModifiedBy());
        assertFalse(actualSetDigitalCookiesPreferenceResult.isFunctionalCookie());
        assertTrue(actualSetDigitalCookiesPreferenceResult.isPerformanceCookie());
        assertTrue(actualSetDigitalCookiesPreferenceResult.isStrictlyAcceptanceCookie());
        assertSame(digitalCustomerProfileId, actualSetDigitalCookiesPreferenceResult.getDigitalCustomerProfileId());
    }

    @Test
    void testSetDigitalCookiesPreference2() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        DigitalCookiesPreferenceRequest cookiesPreferenceRequest = mock(DigitalCookiesPreferenceRequest.class);
        when(cookiesPreferenceRequest.getFunctionalCookie()).thenReturn(true);
        when(cookiesPreferenceRequest.getPerformanceCookie()).thenReturn(true);
        when(cookiesPreferenceRequest.getStrictlyAcceptanceCookie()).thenReturn(true);
        doNothing().when(cookiesPreferenceRequest).setFunctionalCookie(Mockito.<Boolean>any());
        cookiesPreferenceRequest.setFunctionalCookie(false);
        DigitalCookiePreference digitalCookiePreference1 = new DigitalCookiePreference();
        DigitalCookiePreference actualSetDigitalCookiesPreferenceResult = digitalCookiesPreferenceServiceImpl
                .setDigitalCookiesPreference(digitalCustomerProfileId, cookiesPreferenceRequest,
                        digitalCookiePreference1);
        verify(cookiesPreferenceRequest).getFunctionalCookie();
        verify(cookiesPreferenceRequest).getPerformanceCookie();
        verify(cookiesPreferenceRequest).getStrictlyAcceptanceCookie();
        verify(cookiesPreferenceRequest).setFunctionalCookie(Mockito.<Boolean>any());
        assertEquals(userInfoRepository.findUserNameByDigitalCustomerProfileId(digitalCustomerProfileId),
                actualSetDigitalCookiesPreferenceResult.getCookieCreatedBy());
        assertEquals(userInfoRepository.findUserNameByDigitalCustomerProfileId(digitalCustomerProfileId),
                actualSetDigitalCookiesPreferenceResult.getCookieModifiedBy());
        assertTrue(actualSetDigitalCookiesPreferenceResult.isFunctionalCookie());
        assertTrue(actualSetDigitalCookiesPreferenceResult.isPerformanceCookie());
        assertTrue(actualSetDigitalCookiesPreferenceResult.isStrictlyAcceptanceCookie());
        assertSame(digitalCustomerProfileId, actualSetDigitalCookiesPreferenceResult.getDigitalCustomerProfileId());
    }
}