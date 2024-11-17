package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.dto.response.PinHistoryResponse;
import com.unisys.udb.user.exception.PinHistoryRetrievalException;
import com.unisys.udb.user.repository.PinHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PinHistoryServiceImplTests {

    @Mock
    private PinHistoryRepository pinHistoryRepository;

    @InjectMocks
    private PinHistoryServiceImpl pinHistoryService;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testFetchOldPinsSuccess() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        LocalDateTime fromDate = LocalDateTime.now().minusMonths(1);
        LocalDateTime toDate = LocalDateTime.now();
        List<String> oldPins = Arrays.asList("1234", "5678");

        // Mock the behavior of pinHistoryRepository
        when(pinHistoryRepository.findOldPins(any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(oldPins);

        // Act
        PinHistoryResponse response = pinHistoryService.fetchOldPins(digitalCustomerProfileId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getOldPins()).isEqualTo(oldPins);
    }

    @Test
    void testFetchOldPinsDistinctAndSorted() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        List<String> oldPins = Arrays.asList("5678", "1234", "5678", "2345");

        when(pinHistoryRepository.findOldPins(any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(oldPins);

        // Act
        PinHistoryResponse response = pinHistoryService.fetchOldPins(digitalCustomerProfileId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getOldPins()).containsExactly("1234", "2345", "5678");
    }

    @Test
    void testFetchOldPinsFilterBlankPins() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        List<String> oldPins = Arrays.asList("1234", "", "5678", "   ");

        when(pinHistoryRepository.findOldPins(any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(oldPins);

        // Act
        PinHistoryResponse response = pinHistoryService.fetchOldPins(digitalCustomerProfileId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getOldPins()).containsExactly("1234", "5678");
    }

    @Test
    void testFetchOldPinsExceptionHandling() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();

        when(pinHistoryRepository.findOldPins(any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("Test Exception"));

        // Act & Assert
        assertThatThrownBy(() -> pinHistoryService.fetchOldPins(digitalCustomerProfileId))
                .isInstanceOf(PinHistoryRetrievalException.class)
                .hasMessageContaining("Error while retrieving the data for profile ID:");
    }
}
