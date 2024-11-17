package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.dto.request.DigitalAlertRequest;
import com.unisys.udb.user.dto.response.DigitalAlertResponse;
import com.unisys.udb.user.dto.response.UserSuccessResponse;
import com.unisys.udb.user.entity.DigitalCustomerAlert;
import com.unisys.udb.user.entity.DigitalDocdbAlertRef;
import com.unisys.udb.user.exception.DigitalAlertNotFoundException;
import com.unisys.udb.user.exception.InvalidDigitalAlertKeyException;
import com.unisys.udb.user.exception.InvalidDigitalCustomerProfileIdException;
import com.unisys.udb.user.repository.DigitalCustomerAlertRepository;
import com.unisys.udb.user.repository.DigitalCustomerProfileRepository;
import com.unisys.udb.user.repository.DigitalDocdbAlertRefRepository;
import com.unisys.udb.utility.config.DateUtilConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DigitalCustomerAlertServiceImplTest {

    @Mock
    private DigitalCustomerAlertRepository digitalCustomerAlertRepository;

    @Mock
    private DigitalDocdbAlertRefRepository digitalDocdbAlertRefRepository;

    @Mock
    private DigitalCustomerProfileRepository digitalCustomerProfileRepository;

    @InjectMocks
    private DigitalCustomerAlertServiceImpl digitalCustomerAlertService;

    private static final Integer EXPECTED_COUNT = 5; // Example count

    @BeforeEach
    void setDateFormatFromUtil() {
        DateUtilConfig dateUtilConfig = DateUtilConfig.builder()
                                        .dayMonthYearShort("dd/MM/yy")
                                        .dayMonthYearTime("dd/MM/yyyy hh:mm a")
                                        .dayMonthTime("dd MMM hh:mm a")
                                        .dayMonthYear("dd/MM/yyyy").build();
        com.unisys.udb.utility.util.DateUtil.init(dateUtilConfig);
    }

    /**
     * Method under test:
     * {@link DigitalCustomerAlertServiceImpl#getDigitalCustomerAlerts(UUID)}
     */
    @Test
    void testGetDigitalCustomerAlerts() throws DataAccessException {
        UUID customerId = UUID.randomUUID();
        List<DigitalCustomerAlert> customerAlerts = createDigitalCustomerAlertObject();
        when(digitalCustomerAlertRepository.findByDigitalCustomerProfileIdAndAlertReadFlagOrderByAlertCreationDateDesc(
                customerId, false)).thenReturn(customerAlerts);

        List<DigitalAlertResponse> responses = digitalCustomerAlertService.getDigitalCustomerAlerts(customerId);

        verify(digitalCustomerAlertRepository)
                .findByDigitalCustomerProfileIdAndAlertReadFlagOrderByAlertCreationDateDesc(customerId, false);
        assertEquals(2, responses.size());
        assertEquals(customerAlerts.get(0).getDigitalDocdbAlertRef().getDigitalAlertKey(),
                responses.get(0).getDigitalAlertKey());
    }

    private List<DigitalCustomerAlert> createDigitalCustomerAlertObject() {
        List<DigitalCustomerAlert> customerAlerts = new ArrayList<>();
        DigitalCustomerAlert mockAlert = new DigitalCustomerAlert();
        DigitalDocdbAlertRef docdbAlertRef = new DigitalDocdbAlertRef();
        mockAlert.setDigitalCustomerProfileId(UUID.randomUUID());
        mockAlert.setAlertReadFlag(false);
        mockAlert.setAlertCreationDate(LocalDateTime.now());
        docdbAlertRef.setDigitalDocDbAlertRefId(1);
        docdbAlertRef.setDigitalAlertKey("alertInvalidLoginAttempt");
        mockAlert.setDigitalDocdbAlertRef(docdbAlertRef);

        customerAlerts.add(mockAlert);

        DigitalCustomerAlert mockAlert1 = new DigitalCustomerAlert();
        DigitalDocdbAlertRef docdbAlertRef1 = new DigitalDocdbAlertRef();
        mockAlert1.setDigitalCustomerProfileId(UUID.randomUUID());
        mockAlert1.setAlertReadFlag(false);
        mockAlert1.setAlertCreationDate(LocalDateTime.now());
        docdbAlertRef1.setDigitalDocDbAlertRefId(2);
        docdbAlertRef1.setDigitalAlertKey("otheralert");
        mockAlert1.setDigitalDocdbAlertRef(docdbAlertRef1);

        customerAlerts.add(mockAlert1);

        return customerAlerts;
    }

    @Test
    void testCountUnreadUserAlerts() {
        UUID digitalCustomerProfileId = UUID.randomUUID();

        when(digitalCustomerAlertRepository.countUnreadAlertsByProfileId(digitalCustomerProfileId))
                .thenReturn(EXPECTED_COUNT);

        Integer actualCount = digitalCustomerAlertService.countUnreadUserAlerts(digitalCustomerProfileId);

        verify(digitalCustomerAlertRepository).countUnreadAlertsByProfileId(digitalCustomerProfileId);
        assertEquals(EXPECTED_COUNT, actualCount);
    }

    @Test
    void testMarkAlertAsRead() {
        // Given
        DigitalAlertRequest alertRequest = new DigitalAlertRequest();
        alertRequest.setAlertKey("alertKey");
        alertRequest.setDigitalCustomerProfileId(UUID.randomUUID());
        when(digitalCustomerAlertRepository.updateAlertReadFlagByProfileIdAndAlertKey(
                alertRequest.getDigitalCustomerProfileId(), false,
                alertRequest.getAlertKey())).thenReturn(1);

        UserSuccessResponse response = digitalCustomerAlertService.markAlertAsRead(alertRequest);

        assertEquals("Alerts marked as read successfully", response.getMessage());
    }

    @Test
    void testMarkAlertAsReadWhenAlertNotFound() {
        DigitalAlertRequest alertRequest = new DigitalAlertRequest();
        alertRequest.setAlertKey("alertKey");
        alertRequest.setDigitalCustomerProfileId(UUID.randomUUID());

        when(digitalCustomerAlertRepository.updateAlertReadFlagByProfileIdAndAlertKey(
                alertRequest.getDigitalCustomerProfileId(), false,
                alertRequest.getAlertKey())).thenReturn(0);

        DigitalAlertNotFoundException exception =
                assertThrows(DigitalAlertNotFoundException.class, () ->
                        digitalCustomerAlertService.markAlertAsRead(alertRequest));
        assertEquals("Alert not found", exception.getMessage());
    }

    @Test
    void testAlertKeyNotNull() {
        DigitalDocdbAlertRef docdbAlertRef = new DigitalDocdbAlertRef();
        docdbAlertRef.setDigitalAlertKey("testAlertKey");
        String alertKey = (docdbAlertRef != null) ? docdbAlertRef.getDigitalAlertKey() : null;
        assertEquals("testAlertKey", alertKey);
    }

    @Test
    void testAlertKeyNull() {
        DigitalDocdbAlertRef docdbAlertRef = null;
        String alertKey = (docdbAlertRef != null) ? docdbAlertRef.getDigitalAlertKey() : null;
        assertEquals(null, alertKey);
    }

    /**
     * Method under test: {@link DigitalCustomerAlertServiceImpl#saveDigitalCustomerAlert(DigitalAlertRequest)}
     */
    @Test
    void testSaveDigitalCustomerAlertSuccess() {
        DigitalAlertRequest alertRequest = new DigitalAlertRequest();
        alertRequest.setDigitalCustomerProfileId(UUID.randomUUID());
        alertRequest.setAlertKey("testAlertKey");

        DigitalDocdbAlertRef docDbAlertRef = new DigitalDocdbAlertRef();
        docDbAlertRef.setDigitalAlertKey("testAlertKey");

        when(digitalCustomerProfileRepository.existsByDigitalCustomerProfileId(
                any(UUID.class))).thenReturn(true);
        when(digitalDocdbAlertRefRepository.findByDigitalAlertKey("testAlertKey"))
                .thenReturn(docDbAlertRef);

        digitalCustomerAlertService.saveDigitalCustomerAlert(alertRequest);
        verify(digitalCustomerAlertRepository, times(1)).save(any());
    }

    /**
     * Method under test: {@link DigitalCustomerAlertServiceImpl#saveDigitalCustomerAlert(DigitalAlertRequest)}
     */
    @Test
    void testSaveDigitalCustomerAlertWhenInvalidDigitalAlertKeyException() {
        DigitalAlertRequest alertRequest = new DigitalAlertRequest();
        alertRequest.setDigitalCustomerProfileId(UUID.randomUUID());
        alertRequest.setAlertKey("nonExistentKey");

        when(digitalCustomerProfileRepository.existsByDigitalCustomerProfileId(
                any(UUID.class))).thenReturn(true);
        when(digitalDocdbAlertRefRepository.findByDigitalAlertKey("nonExistentKey"))
                .thenReturn(null);

        assertThrows(InvalidDigitalAlertKeyException.class, () -> {
            digitalCustomerAlertService.saveDigitalCustomerAlert(alertRequest);
        });
    }

    /**
     * Method under test: {@link DigitalCustomerAlertServiceImpl#saveDigitalCustomerAlert(DigitalAlertRequest)}
     */
    @Test
    void testSaveDigitalCustomerAlertWhenInvalidDigitalCustomerProfileIdException() {
        DigitalAlertRequest alertRequest = new DigitalAlertRequest();
        alertRequest.setDigitalCustomerProfileId(UUID.randomUUID());

        when(digitalCustomerProfileRepository.existsByDigitalCustomerProfileId(
                any(UUID.class))).thenReturn(false);

        assertThrows(InvalidDigitalCustomerProfileIdException.class, () -> {
            digitalCustomerAlertService.saveDigitalCustomerAlert(alertRequest);
        });
    }
}
