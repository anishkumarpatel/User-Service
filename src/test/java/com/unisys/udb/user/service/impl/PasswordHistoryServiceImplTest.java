package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.dto.response.OldPasswordHistoryResponse;
import com.unisys.udb.user.exception.PasswordHistoryRetrievalException;
import com.unisys.udb.user.repository.PasswordHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class PasswordHistoryServiceImplTest {

    private static final int SIX = 6;
    @Mock
    private PasswordHistoryRepository passwordHistoryRepository;
    @InjectMocks
    private PasswordHistoryServiceImpl passwordHistoryService;
    private UUID digitalCustomerProfileId;

    @BeforeEach
    void setUp() {
        digitalCustomerProfileId = UUID.randomUUID();
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testFetchOldPasswordsWithOldPasswords() {
        when(passwordHistoryRepository.findOldPasswordsByDigitalCustomerProfileIdAndDateRange(Mockito.<UUID>any(),
                Mockito.<LocalDateTime>any(), Mockito.<LocalDateTime>any())).thenReturn(new ArrayList<>());
        OldPasswordHistoryResponse response = passwordHistoryService.fetchOldPasswords(digitalCustomerProfileId);
        verify(passwordHistoryRepository).findOldPasswordsByDigitalCustomerProfileIdAndDateRange(Mockito.<UUID>any(),
                Mockito.<LocalDateTime>any(), Mockito.<LocalDateTime>any());
        assertTrue(response.getOldPasswordList().isEmpty());
        assertEquals(new ArrayList<>(), response.getOldPasswordList());
    }

    @Test
    void testFetchOldPasswordsWithNoOldPasswords() {
        LocalDateTime fromDate = LocalDateTime.now().minusMonths(SIX);
        LocalDateTime toDate = LocalDateTime.now();
        when(passwordHistoryRepository.findOldPasswordsByDigitalCustomerProfileIdAndDateRange(
                digitalCustomerProfileId, fromDate, toDate))
                .thenReturn(Collections.emptyList());
        OldPasswordHistoryResponse response = passwordHistoryService.fetchOldPasswords(digitalCustomerProfileId);
        assertEquals(Collections.emptyList(), response.getOldPasswordList());
    }

    @Test
    void testFetchOldPasswordsWithNullOldPasswords() {
        when(passwordHistoryRepository.findOldPasswordsByDigitalCustomerProfileIdAndDateRange(
                any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(null);

        OldPasswordHistoryResponse response = passwordHistoryService.fetchOldPasswords(digitalCustomerProfileId);
        verify(passwordHistoryRepository).findOldPasswordsByDigitalCustomerProfileIdAndDateRange(
                any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class));
        assertTrue(response.getOldPasswordList().isEmpty());
        assertEquals(Collections.emptyList(), response.getOldPasswordList());
    }

    @Test
    void testFetchOldPasswordsWithException() {
        UUID random = UUID.randomUUID();
        when(passwordHistoryRepository.findOldPasswordsByDigitalCustomerProfileIdAndDateRange(Mockito.<UUID>any(),
                Mockito.<LocalDateTime>any(), Mockito.<LocalDateTime>any()))
                .thenThrow(new PasswordHistoryRetrievalException("An error occurred", new Throwable()));
        assertThrows(PasswordHistoryRetrievalException.class,
                () -> passwordHistoryService.fetchOldPasswords(random));
        verify(passwordHistoryRepository).findOldPasswordsByDigitalCustomerProfileIdAndDateRange(Mockito.<UUID>any(),
                Mockito.<LocalDateTime>any(), Mockito.<LocalDateTime>any());
    }
}
