package com.unisys.udb.user.entity;

import com.unisys.udb.user.constants.SchemaConstants;
import com.unisys.udb.user.constants.UdbConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "digital_customer_profile", schema = SchemaConstants.DIGITAL)
public class DigitalCustomerProfile {
    @Column(name = "digital_customer_profile_id", nullable = false)
    @Id
    private UUID digitalCustomerProfileId;
    @Column(name = "core_customer_profile_id", nullable = false, unique = true)
    private UUID coreCustomerProfileId;
    @Column(name = "digital_customer_status_type_ref_id", nullable = false)
    private int digitalCustomerStatusTypeId;
    @Column(name = "digital_user_name", nullable = false, length = UdbConstants.MAX_LENGTH_FIFTY, unique = true)
    private String digitalUserName;
    @Column(name = "digital_account_status_reason", length = UdbConstants.MAX_LENGTH_1000)
    private String digitalAccountStatusReason;
    @Column(name = "profile_registration_date", nullable = false)
    private LocalDateTime registrationDate;
    @Column(name = "profile_created_by", nullable = false, length = UdbConstants.MAX_LENGTH_FIFTY)
    private String profileCreatedBy;
    @Column(name = "profile_modification_date")
    private LocalDateTime profileModificationDate;
    @Column(name = "profile_modified_by", length = UdbConstants.MAX_LENGTH_FIFTY)
    private String profileModifiedBy;
    @Column(name = "pin_set_completed", nullable = false)
    private boolean pinSetCompleted;
    @Column(name = "mfa_activity_completed", nullable = false)
    private boolean mfaActivityCompleted;
    @Column(name = "pin_expiry_date", nullable = false)
    private LocalDateTime pinExpiryDate;
    @Column(name = "pwd_expiry_date", nullable = false)
    private LocalDateTime pwdExpiryDate;
    @OneToMany(mappedBy = "digitalCustomerProfile")
    private List<DigitalDeviceLink> digitalDeviceLink;
}
