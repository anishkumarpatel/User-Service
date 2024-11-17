package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.request.PinChangeRequest;
import com.unisys.udb.user.dto.response.ComparePinResponse;
import com.unisys.udb.user.dto.response.PinChangeResponse;
import com.unisys.udb.user.entity.DigitalCustomerPin;
import com.unisys.udb.user.exception.PinHistoryRetrievalException;
import com.unisys.udb.user.exception.PinValidationException;
import com.unisys.udb.user.repository.PinRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.unisys.udb.user.constants.UdbConstants.NINTEEN_SEVENTY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PinServiceImpl.class})
@ExtendWith(SpringExtension.class)
public class PinServiceImplTest {
    public static final int EXPECTED = 304;
    public static final int OK_STATUS = 200;
    @Autowired
    private PinServiceImpl pinServiceImpl;
    @MockBean
    private PinRepository pinRepository;

    @Test
    void validateNewPin() throws PinValidationException {
        PinChangeRequest pinChangeRequest = PinChangeRequest.builder()
                .digitalCustomerProfileId(UUID.randomUUID())
                .oldPin("123456")
                .newPin("561234")
                .build();

        List<DigitalCustomerPin> pinHistoryList = new ArrayList<>();
        when(pinRepository.getPinHistory(pinChangeRequest.getDigitalCustomerProfileId())).thenReturn(pinHistoryList);
        PinChangeResponse pinChangeResponse = pinServiceImpl.validatePinHistory(pinChangeRequest);
        assertEquals(OK_STATUS, pinChangeResponse.getStatusCode());
    }

    @Test
    void validateNewSamePin() throws PinValidationException {

        PinChangeRequest pinChangeRequest = PinChangeRequest.builder()
                .digitalCustomerProfileId(UUID.randomUUID())
                .oldPin("123456")
                .newPin("123456")
                .build();
        List<DigitalCustomerPin> pinHistoryList = new ArrayList<>();
        when(pinRepository.getPinHistory(pinChangeRequest.getDigitalCustomerProfileId())).thenReturn(pinHistoryList);
        PinChangeResponse pinChangeResponse = pinServiceImpl.validatePinHistory(pinChangeRequest);
        assertEquals(EXPECTED, pinChangeResponse.getStatusCode());
        assertEquals("SHOULD_NOT_BE_SAME", pinChangeResponse.getMessage());
    }

    @Test
    void saveOldPin() {
        PinChangeRequest pinChangeRequest = PinChangeRequest.builder()
                .digitalCustomerProfileId(UUID.randomUUID())
                .oldPin("123456")
                .newPin("561234")
                .build();
        DigitalCustomerPin digital = new DigitalCustomerPin();
        when(pinRepository.save(any(DigitalCustomerPin.class))).thenReturn(digital);
        PinChangeResponse pinChangeResponse = pinServiceImpl.saveOldPin(pinChangeRequest);
        assertEquals(HttpStatus.OK.value(), pinChangeResponse.getStatusCode());
    }

    @Test
    void testComparePin() {
        when(pinRepository.getPinHistory(Mockito.<UUID>any())).thenReturn(new ArrayList<>());
        ComparePinResponse actualComparePinResult = pinServiceImpl.comparePin(UUID.randomUUID(), "New Pin");
        verify(pinRepository).getPinHistory(Mockito.<UUID>any());
        assertEquals("Pin Not Matched", actualComparePinResult.getPinMatchedResponse());
    }

    /**
     * Method under test: {@link PinServiceImpl#comparePin(UUID, String)}
     */
    @Test
    void testComparePinSuccess() {
        DigitalCustomerPin digitalCustomerPin = new DigitalCustomerPin();
        digitalCustomerPin.setDigitalCustomerPinId(1);
        digitalCustomerPin.setDigitalCustomerProfileId(UUID.randomUUID());
        digitalCustomerPin.setOldPin("Comparing new pin for digitalCustomerProfileId: {}");
        digitalCustomerPin.setPinChangeDate(LocalDate.of(NINTEEN_SEVENTY, 1, 1).atStartOfDay());
        digitalCustomerPin.setPinCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        digitalCustomerPin.setPinCreationDate(LocalDate.of(NINTEEN_SEVENTY, 1, 1).atStartOfDay());
        digitalCustomerPin.setPinExpiryDate(LocalDate.of(NINTEEN_SEVENTY, 1, 1).atStartOfDay());
        digitalCustomerPin.setPinModificationDate(LocalDate.of(NINTEEN_SEVENTY, 1, 1).atStartOfDay());
        digitalCustomerPin.setPinModifiedBy("Jan 1, 2020 9:00am GMT+0100");

        ArrayList<DigitalCustomerPin> digitalCustomerPinList = new ArrayList<>();
        digitalCustomerPinList.add(digitalCustomerPin);
        UUID randomUUID = UUID.randomUUID();
        when(pinRepository.getPinHistory(randomUUID)).thenReturn(digitalCustomerPinList);
        assertThrows(PinHistoryRetrievalException.class, () -> pinServiceImpl.comparePin(randomUUID, "New Pin"));
        verify(pinRepository).getPinHistory(randomUUID);
    }

    /**
     * Method under test:  {@link PinServiceImpl#comparePin(UUID, String)}
     */
    @Test
    void testComparePinNotSuccess() {
        UUID randomUUID = UUID.randomUUID();

        when(pinRepository.getPinHistory(randomUUID))
                .thenThrow(new PinHistoryRetrievalException("An error occurred", new Throwable()));

        assertThrows(PinHistoryRetrievalException.class, () -> pinServiceImpl.comparePin(randomUUID, "New Pin"));

        verify(pinRepository).getPinHistory(randomUUID);
    }

    @Test
    void testComparePinException() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        String newPin = "123456";

        RuntimeException testException = new RuntimeException("Test Exception");
        when(pinRepository.getPinHistory(digitalCustomerProfileId)).thenThrow(testException);

        // Act & Assert
        assertThatThrownBy(() -> pinServiceImpl.comparePin(digitalCustomerProfileId, newPin))
                .isInstanceOf(PinHistoryRetrievalException.class)
                .hasMessageContaining("Failed to retrieve pin history")
                .hasCause(testException);
    }
}
