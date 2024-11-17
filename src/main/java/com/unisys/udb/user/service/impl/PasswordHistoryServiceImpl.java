package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.dto.response.OldPasswordHistoryResponse;
import com.unisys.udb.user.exception.PasswordHistoryRetrievalException;
import com.unisys.udb.user.repository.PasswordHistoryRepository;
import com.unisys.udb.user.service.PasswordHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordHistoryServiceImpl implements PasswordHistoryService {

    private final PasswordHistoryRepository passwordHistoryRepository;

    @Value("${password.history.period}")
    private int passwordHistoryPeriod;

    /**
     * Fetches old passwords for a given digital customer profile ID.
     *
     * @param digitalCustomerProfileId The UUID of the digital customer profile
     * @return An OldPasswordHistoryResponse containing the list of old passwords
     * @throws PasswordHistoryRetrievalException If an error occurs during password retrieval
     */
    @Override
    public OldPasswordHistoryResponse fetchOldPasswords(UUID digitalCustomerProfileId) {
        try {
            log.debug("Fetching old passwords for profile ID: {}", digitalCustomerProfileId);
            LocalDateTime fromDate = LocalDateTime.now().minusDays(passwordHistoryPeriod);
            LocalDateTime toDate = LocalDateTime.now();

            List<String> oldPasswords =
                    passwordHistoryRepository.findOldPasswordsByDigitalCustomerProfileIdAndDateRange(
                            digitalCustomerProfileId, fromDate, toDate);
            log.debug("Old passwords fetched successfully for profile ID: {}", digitalCustomerProfileId);
            return new OldPasswordHistoryResponse(oldPasswords != null ? oldPasswords : Collections.emptyList());
        } catch (Exception ex) {
            log.error("Error while retrieving old passwords for profile ID: {}", digitalCustomerProfileId, ex);
            throw new PasswordHistoryRetrievalException("Error while retrieving the data for profile ID: ", ex);
        }
    }
}
