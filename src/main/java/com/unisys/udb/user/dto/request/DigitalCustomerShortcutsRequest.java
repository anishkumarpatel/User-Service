package com.unisys.udb.user.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class DigitalCustomerShortcutsRequest implements Serializable {

    @JsonProperty(value = "fundTransferShortcut", required = false)
    private Boolean fundTransferShortcut;

    @JsonProperty(value = "estatementShortcut", required = false)
    private Boolean estatementShortcut;

    @JsonProperty(value = "payeeShortcut", required = false)
    private Boolean payeeShortcut;

    @JsonProperty(value = "scheduledPaymentsShortcut", required = false)
    private Boolean scheduledPaymentsShortcut;

    @JsonProperty(value = "commPrefShortcut", required = false)
    private Boolean commPrefShortcut;

    @JsonProperty(value = "sessionHistoryShortcut", required = false)
    private Boolean sessionHistoryShortcut;

}