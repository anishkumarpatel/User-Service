package com.unisys.udb.user.entity;

import com.unisys.udb.user.constants.UdbConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "digital_cookie_preference", schema = "digital")
public class DigitalCookiePreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "digital_cookie_preference_id", nullable = false)
    private int digitalCookiePreferenceId;
    @Column(name = "digital_customer_profile_id", nullable = false)
    private UUID digitalCustomerProfileId;
    @Column(name = "strictly_acceptance_cookie", nullable = false)
    private boolean strictlyAcceptanceCookie;
    @Column(name = "performance_cookie", nullable = false)
    private boolean performanceCookie;
    @Column(name = "functional_cookie", nullable = false)
    private boolean functionalCookie;
    @Column(name = "cookie_creation_date", nullable = false)
    private LocalDateTime cookieCreationDate;
    @Column(name = "cookie_created_by", nullable = false, length = UdbConstants.MAX_LENGTH_FIFTY)
    private String cookieCreatedBy;
    @Column(name = "cookie_modification_date")
    private LocalDateTime cookieModificationDate;
    @Column(name = "cookie_modified_by", length = UdbConstants.MAX_LENGTH_FIFTY)
    private String cookieModifiedBy;
}
