package com.unisys.udb.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarketingPreferenceResponse {
    private String marketingTypeElementName;
    private String marketingDescElementName;
    private Boolean marketingFlag;
}
