package com.unisys.udb.user.controller;

import com.unisys.udb.user.dto.request.DigitalAlertRequest;
import com.unisys.udb.user.dto.response.DigitalAlertResponse;
import com.unisys.udb.user.dto.response.UserSuccessResponse;
import com.unisys.udb.user.entity.DigitalCustomerAlert;
import com.unisys.udb.user.entity.DigitalDocdbAlertRef;
import com.unisys.udb.user.service.DigitalCustomerAlertService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAlertControllerTest {

    @InjectMocks
    private UserAlertController userAlertController;

    @Mock
    private DigitalCustomerAlertService digitalCustomerAlertService;

    private static final Integer EXPECTED_UNREAD_COUNT = 3;

    /**
     * Method under test: {@link UserAlertController#getDigitalCustomerAlerts(UUID)}
     */
    @Test
    void testGetDigitalCustomerAlerts() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        List<DigitalAlertResponse> expectedAlerts = createDigitalAlertResponse();

        when(digitalCustomerAlertService.getDigitalCustomerAlerts(digitalCustomerProfileId))
                .thenReturn(expectedAlerts);
        ResponseEntity<List<DigitalAlertResponse>> responseEntity = userAlertController
                .getDigitalCustomerAlerts(digitalCustomerProfileId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedAlerts.size(), responseEntity.getBody().size());
    }

    private List<DigitalAlertResponse> createDigitalAlertResponse() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String now = LocalDateTime.now().format(formatter);
        List<String> params = new ArrayList<>();
        params.add("1");
        return Arrays.asList(
                new DigitalAlertResponse("invalid", false, "12/12/2023",
                        params),
                new DigitalAlertResponse("loginfail", false, "12/12/2023",
                        params),
                new DigitalAlertResponse("msgGovern", false, "12/12/2023",
                        params)
        );
    }

    @Test
    void testGetUnreadAlertCounts() {
        UUID digitalCustomerProfileId = UUID.randomUUID();

        when(digitalCustomerAlertService.countUnreadUserAlerts(digitalCustomerProfileId))
                .thenReturn(EXPECTED_UNREAD_COUNT);

        ResponseEntity<Integer> responseEntity = userAlertController.getUnreadAlertCounts(digitalCustomerProfileId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode()); // Check for HTTP 200 status
        assertEquals(EXPECTED_UNREAD_COUNT, responseEntity.getBody()); // Check the count in the body
    }

    @Test
    void testMarkAlertAsRead() {
        // Given
        DigitalAlertRequest alertRequest = new DigitalAlertRequest();
        UserSuccessResponse userSuccessResponse = new UserSuccessResponse();
        when(digitalCustomerAlertService
                .markAlertAsRead(alertRequest)).thenReturn(userSuccessResponse);

        ResponseEntity<UserSuccessResponse> response = userAlertController.markAlertAsRead(alertRequest);
        assertEquals(ResponseEntity.ok(userSuccessResponse), response);
    }

    /**
     * Method under test: {@link UserAlertController#saveDigitalCustomerAlert(DigitalAlertRequest)}
     */
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
        return customerAlerts;
    }
    @Test
    void testSaveDigitalCustomerAlertWhenSuccess() {
        DigitalAlertRequest alertRequest = new DigitalAlertRequest();
        ResponseEntity<UserSuccessResponse> responseEntity = userAlertController.saveDigitalCustomerAlert(alertRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Alert saved successfully", responseEntity.getBody().getMessage());
        verify(digitalCustomerAlertService, times(1)).saveDigitalCustomerAlert(alertRequest);
    }
}
