package com.unisys.udb.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationOrchestratorRequest {
    private Map<String, String> requiredFieldsMap;
    private Map<String, String> extendedFieldsMap;
}
