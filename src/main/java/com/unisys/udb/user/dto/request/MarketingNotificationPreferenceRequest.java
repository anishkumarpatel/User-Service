package com.unisys.udb.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MarketingNotificationPreferenceRequest {
    private Map<String, Boolean> marketingPreferenceList;
}