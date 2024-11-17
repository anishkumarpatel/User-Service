package com.unisys.udb.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleEngineResponse {
    private String country;
    private String url;
    private Boolean evidenceRequired;
}
