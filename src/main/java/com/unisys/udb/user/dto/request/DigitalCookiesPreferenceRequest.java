package com.unisys.udb.user.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class DigitalCookiesPreferenceRequest {

    @JsonProperty(value = "strictlyAcceptanceCookie", required = false)
    private Boolean strictlyAcceptanceCookie;
    @JsonProperty(value = "performanceCookie", required = false)
    private Boolean performanceCookie;
    @JsonProperty(value = "functionalCookie", required = false)
    private Boolean functionalCookie;
}

