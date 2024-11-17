package com.unisys.udb.user.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TermsConditionsAndCookiesRequest {

    @JsonProperty(value = "digitalDeviceUdid", required = false)
    private String digitalDeviceUdid;
    @NotBlank(message = "deviceName should not be null")
    @JsonProperty(value = "deviceName", required = true)
    private String deviceName;
    @JsonProperty(value = "deviceType", required = false)
    private String deviceType;
    @NotBlank(message = "deviceOsVersion should not be null")
    @JsonProperty(value = "deviceOsVersion", required = true)
    private String deviceOsVersion;
    @JsonProperty(value = "strictlyAcceptanceCookie", required = false)
    private Boolean strictlyAcceptanceCookie;
    @JsonProperty(value = "performanceCookie", required = false)
    private Boolean performanceCookie;
    @JsonProperty(value = "functionalCookie", required = false)
    private Boolean functionalCookie;
    @JsonProperty(value = "termsConditions", required = true)
    private Boolean termsConditions;
    @JsonProperty(value = "digitalCustomerDeviceId", required = false)
    private Integer digitalCustomerDeviceId;
    @JsonProperty(value = "deviceToken", required = false)
    private String deviceToken;
    @JsonProperty(value = "deviceStatus", required = false)
    private Boolean deviceStatus;
    @JsonProperty(value = "deviceCreationDate", required = false)
    private String deviceCreationDate;
    @JsonProperty(value = "deviceCreatedBy", required = false)
    private String deviceCreatedBy;
    @JsonProperty(value = "deviceModificationDate", required = false)
    private String deviceModificationDate;
    @JsonProperty(value = "deviceModificationBy", required = false)
    private String deviceModificationBy;
}
