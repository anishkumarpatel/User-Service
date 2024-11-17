package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.dto.response.PinHistoryResponse;
import com.unisys.udb.user.exception.PinHistoryRetrievalException;
import com.unisys.udb.user.repository.PinHistoryRepository;
import com.unisys.udb.user.service.PinHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PinHistoryServiceImpl implements PinHistoryService {

    private final PinHistoryRepository pinHistoryRepository;

    @Value("${pin.history.period}")
    private int pinHistoryPeriod;

    /**
     * Fetches old pins for a given digital customer profile ID.
     *
     * @param digitalCustomerProfileId The UUID of the digital customer profile
     * @return A PinHistoryResponse containing the list of old pins
     * @throws PinHistoryRetrievalException If an error occurs during pin retrieval
     */
    @Override
    public PinHistoryResponse fetchOldPins(UUID digitalCustomerProfileId) {
        try {
            log.debug("Fetching old pins for profile ID: {}", digitalCustomerProfileId);
            LocalDateTime fromDate = LocalDateTime.now().minusDays(pinHistoryPeriod);
            LocalDateTime toDate = LocalDateTime.now();
            List<String> oldPins = pinHistoryRepository.findOldPins(digitalCustomerProfileId, fromDate, toDate)
                    .stream()
                    .filter(pin -> !pin.isBlank()) // Filter out blank pins
                    .distinct() // Remove duplicate pins
                    .sorted() // Sort pins in natural order
                    .toList(); // Replace collect(Collectors.toList()) with toList()
            log.debug("Old pins fetched successfully for profile ID: {}", digitalCustomerProfileId);
            return new PinHistoryResponse(oldPins);
        } catch (Exception ex) {
            log.error("Error while retrieving old pins for profile ID: {}", digitalCustomerProfileId, ex);
            throw new PinHistoryRetrievalException("Error while retrieving the data for profile ID: ", ex);
        }
    }
}