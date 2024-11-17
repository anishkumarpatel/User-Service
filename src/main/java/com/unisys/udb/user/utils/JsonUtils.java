package com.unisys.udb.user.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisys.udb.utility.auditing.dto.CustomerActionAuditHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class JsonUtils {

    private final CustomerActionAuditHolder customerActionAuditHolder;
    private final ObjectMapper objectMapper;

    @Autowired
    public JsonUtils(CustomerActionAuditHolder customerActionAuditHolder, ObjectMapper objectMapper) {
        this.customerActionAuditHolder = customerActionAuditHolder;
        this.objectMapper = objectMapper;
    }

    public String convertToJson(Object object) throws JsonProcessingException {
        // Convert the object to a JSON string with proper formatting
        return objectMapper.writeValueAsString(object);
    }

    public void logAndAuditAction(UUID digitalCustomerProfileId, Object request, String createdBy) {
        try {
            String requestJson = convertToJson(request);
            customerActionAuditHolder.setDigitalCustomerId(digitalCustomerProfileId);
            customerActionAuditHolder.setAuditCreatedBy(createdBy);
            customerActionAuditHolder.setNewJsonObject(requestJson);
        } catch (JsonProcessingException e) {
            log.error("Error converting request to JSON: {}", e.getMessage());
            // Handle exception as necessary
        }
    }
}
