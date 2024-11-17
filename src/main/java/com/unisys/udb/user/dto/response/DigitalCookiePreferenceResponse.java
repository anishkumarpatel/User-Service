package com.unisys.udb.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DigitalCookiePreferenceResponse {
    private boolean strictlyAcceptanceCookie;
    private boolean performanceCookie;
    private boolean functionalCookie;
    private LocalDateTime cookieCreationDate;
    private String cookieCreatedBy;
    private LocalDateTime cookieModificationDate;
    private String cookieModifiedBy;
}
