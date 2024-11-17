
package com.unisys.udb.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;

import com.unisys.udb.user.service.impl.UserInfoServiceImpl;
import com.unisys.udb.user.utils.dto.response.NotificationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisys.udb.user.dto.request.PayeeRequest;
import com.unisys.udb.user.dto.response.DigitalCustomerPayeeResponse;
import com.unisys.udb.user.service.DigitalPayeeService;
import com.unisys.udb.user.utils.schemavalidation.PayeeSchemaValidation;

@ContextConfiguration(classes = { PayeeController.class })
@ExtendWith(SpringExtension.class)
class PayeeControllerTest {
    @MockBean
    private DigitalPayeeService digitalPayeeService;

    @MockBean
    private ObjectMapper objectMapper;

    @Autowired
    private PayeeController payeeController;

    @MockBean
    private NotificationUtil notificationUtil;
    @MockBean
    private PayeeSchemaValidation payeeSchemaValidation;

    @MockBean
    private UserInfoServiceImpl userInfoService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Method under test: {@link PayeeController#addPayee(PayeeRequest)}
     */
    @Test
    void testAddPayee() throws Exception {
        // Arrange
        doNothing().when(digitalPayeeService).createPayee(Mockito.<PayeeRequest>any());
        doNothing().when(payeeSchemaValidation).validateSchema(Mockito.<PayeeRequest>any());

        PayeeRequest payeeRequest = new PayeeRequest();
        payeeRequest.setDigitalCustomerProfileId(UUID.fromString("1e7c2827-cdba-42d6-ad12-33ef8313b9c4"));
        payeeRequest.setPayeeAccountNumber("42");
        payeeRequest.setPayeeBankCode("Payee Bank Code");
        payeeRequest.setPayeeBankName("Payee Bank Name");
        payeeRequest.setPayeeName("Payee Name");
        payeeRequest.setPayeeNickname("Payee Nickname");
        payeeRequest.setPayeeReference("Payee Reference");
        String content = (new ObjectMapper()).writeValueAsString(payeeRequest);
        MockHttpServletRequestBuilder requestBuilder = post("/api/v1/payee/addPayee")
                .contentType(MediaType.APPLICATION_JSON).content(content);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(payeeController).build().perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"digitalCustomerProfileId\":\"1e7c2827-cdba-42d6-ad12-33ef8313b9c4\","
                                + "\"payeeName\":"
                                + "\"Payee Name\",\"payeeBankName\":\"Payee Bank Name\",\"payeeAccountNumber\":\"42\","
                                + "\"payeeNickname\":\"Payee Nickname\","
                                + "\"payeeBankCode\":\"Payee Bank Code\",\"payeeReference\":\"Payee Reference\","
                                + "\"response\":\"Payee created successfully\","
                                + "\"httpStatusCode\":200}"));
    }

    @Test
    void testGetAllPayees() {
        // Arrange
        UUID digitalCustomerProfileId = UUID.randomUUID();
        String sortBy = "name";

        // Mock service
        when(digitalPayeeService.getAllPayees(digitalCustomerProfileId, sortBy))
                .thenReturn(Collections.singletonList(new DigitalCustomerPayeeResponse()));

        // Act
        ResponseEntity<List<DigitalCustomerPayeeResponse>> response = payeeController
                .getAllPayees(digitalCustomerProfileId, sortBy);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        when(digitalPayeeService.getAllPayees(digitalCustomerProfileId, null))
                .thenReturn(Collections.singletonList(new DigitalCustomerPayeeResponse()));
        ResponseEntity<List<DigitalCustomerPayeeResponse>> responseNoSort = payeeController
                .getAllPayees(digitalCustomerProfileId, null);
        assertEquals(HttpStatus.OK, responseNoSort.getStatusCode());
        assertNotNull(responseNoSort.getBody());
        assertEquals(1, responseNoSort.getBody().size());
    }

    @Test
    void testUpdatePayee() {
        Integer digitalPayeeId = 1;
        String payeeNickName = "PPP";
        String payeeReference = "YYY";
        when(digitalPayeeService.updatePayeeDetails(anyInt(), anyString(), anyString(),
                anyString())).thenReturn(true);
        ResponseEntity<Object> data = payeeController.updatePayeeDetails(digitalPayeeId, payeeNickName, payeeReference,
                  "BBB");

        assertNotNull(data);
        payeeNickName = "121";
        payeeController.updatePayeeDetails(digitalPayeeId, payeeNickName, payeeReference, "BBB");
        verify(digitalPayeeService, times(1)).updatePayeeDetails(digitalPayeeId,
                payeeNickName, payeeReference, "BBB");
        when(digitalPayeeService.updatePayeeDetails(anyInt(), anyString(), anyString(),
                anyString())).thenReturn(false);
        ResponseEntity<Object> data1 = payeeController.updatePayeeDetails(0, "CCC",
                payeeReference, "BBB");
        assertNotNull(data1);

    }
    @Test
    void testGetFundTransferOptions() throws Exception {
        // Arrange
        when(digitalPayeeService.getFundTransferDropdownValues(Mockito.<String>any())).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/payee/fund-transfer/options/{languageCode}", "en");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(payeeController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }
    @Test
    void testGetPayeeTrxs() {
        Integer digitalPayeeId = 1;

        payeeController.viewPayeeDetails(digitalPayeeId);
        verify(digitalPayeeService, times(1)).getPayeeData(digitalPayeeId);
    }

    @Test
    void testDeletePayee() {
        List<Integer> digitalCustomerPayeeIds = Arrays.asList(1, 2);
        digitalPayeeService.deletePayeeServe(digitalCustomerPayeeIds);

        ResponseEntity<List<DigitalCustomerPayeeResponse>> response = payeeController.
                deletePayee(digitalCustomerPayeeIds);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testSendNotification() {
        UUID digitalCustomerProfileId = UUID.randomUUID();
        String digitalUserName = "testUser";
        String eventSource = "source";
        String activity = "activity";
        String template = "template";
        String languagePreference = "en";
        String deviceId = "device123";

        Map<String, String> requiredFieldsMap = new HashMap<>();
        requiredFieldsMap.put("digitalCustomerProfileId", digitalCustomerProfileId.toString());
        requiredFieldsMap.put("digitalUserName", digitalUserName);
        requiredFieldsMap.put("eventSource", eventSource);
        requiredFieldsMap.put("activity", activity);
        requiredFieldsMap.put("NOTIFICATION_TEMPLATE_NAME", template);
        requiredFieldsMap.put("languagePreference", languagePreference);
        requiredFieldsMap.put("deviceId", deviceId);

        when(notificationUtil.prepareRequiredFieldsMap(digitalCustomerProfileId, digitalUserName, eventSource, activity,
                template, languagePreference))
                .thenReturn(requiredFieldsMap);

        ResponseEntity<Void> response = payeeController.sendNotification1(digitalCustomerProfileId, digitalUserName,
                eventSource, activity, template, languagePreference, deviceId);

        assertEquals(ResponseEntity.ok().build(), response);
    }
    @Test
    void testUpdatePayeeDetails()  {

         int digitalCustomerPayeeId2 = 2;

         String userName2 = "User2";
         ResponseEntity<Object> response2 = payeeController.updatePayeeDetails(digitalCustomerPayeeId2, null, null,
                   userName2);

         assertNotNull(response2);

    }
}