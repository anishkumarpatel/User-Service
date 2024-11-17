package com.unisys.udb.user.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisys.udb.user.dto.request.NotificationRequest;
import com.unisys.udb.utility.auditing.dto.CustomerActionAuditHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JsonTest {

     @Mock
     private ObjectMapper objectMapper;

     @Mock
     private CustomerActionAuditHolder customerActionAuditHolder;

     private JsonUtils jsonUtils;

     @BeforeEach
     void setUp() {
         MockitoAnnotations.openMocks(this);
         jsonUtils = new JsonUtils(customerActionAuditHolder, objectMapper);
     }

     @Test
     void testLogAndAuditActionSuccess() throws JsonProcessingException {
         // Given
         UUID digitalCustomerProfileId = UUID.randomUUID();
         Object request = new Object();
         String createdBy = "User123";
         String expectedJson = "{\"key\":\"value\"}";

         when(objectMapper.writeValueAsString(request)).thenReturn(expectedJson);

         // When
         jsonUtils.logAndAuditAction(digitalCustomerProfileId, request, createdBy);

         // Then
         verify(objectMapper).writeValueAsString(request);
         verify(customerActionAuditHolder).setDigitalCustomerId(digitalCustomerProfileId);
         verify(customerActionAuditHolder).setAuditCreatedBy(createdBy);
         verify(customerActionAuditHolder).setNewJsonObject(expectedJson);
     }

     @Test
     void testLogAndAuditActionFailure() throws JsonProcessingException {
         // Given
         UUID digitalCustomerProfileId = UUID.randomUUID();
         Object request = new Object();
         String createdBy = "User123";

         when(objectMapper.writeValueAsString(request)).thenThrow(new JsonProcessingException("Error") {
         });

         // When & Then
         assertDoesNotThrow(() -> jsonUtils.logAndAuditAction(digitalCustomerProfileId, request, createdBy));
         verify(customerActionAuditHolder, never()).setNewJsonObject(anyString());
     }

    @Test
     void testDeserialize() {
        // Given
        String jsonString = "{\"digitalCustomerProfileId\":\"66C619AB-D893-48CE-9E4D-DB51758AA262\"}";

        // When
        NotificationRequest notificationRequest = Json.deserialize(jsonString, NotificationRequest.class);

        // Then
        assertNotNull(notificationRequest);
        assertEquals(UUID.fromString("66C619AB-D893-48CE-9E4D-DB51758AA262"),
                notificationRequest.getDigitalCustomerProfileId());
    }
}
