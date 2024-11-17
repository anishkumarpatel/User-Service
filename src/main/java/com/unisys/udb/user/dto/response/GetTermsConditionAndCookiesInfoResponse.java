package com.unisys.udb.user.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class GetTermsConditionAndCookiesInfoResponse {
    private Boolean termsAndConditions;
    private Boolean strictlyAcceptanceCookie;
    private Boolean performanceCookie;
    private Boolean functionalCookie;
}
