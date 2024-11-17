package com.unisys.udb.user.entity;

import com.unisys.udb.user.constants.UdbConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "digital_customer_pin", schema = "digital")
public class DigitalCustomerPin {

    @Id
    @Column(name = "digital_customer_pin_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer digitalCustomerPinId;
    @Column(name = "digital_customer_profile_id", nullable = false)
    private UUID digitalCustomerProfileId;
    @Column(name = "old_pin", nullable = false, length = UdbConstants.MAX_LENGTH_250)
    private String oldPin;
    @Column(name = "pin_change_date", nullable = false)
    private LocalDateTime pinChangeDate;
    @Column(name = "pin_expiry_date", nullable = false)
    private LocalDateTime pinExpiryDate;
    @Column(name = "pin_creation_date", nullable = false)
    private LocalDateTime pinCreationDate;
    @Column(name = "pin_created_by", length = UdbConstants.MAX_LENGTH_FIFTY, nullable = false)
    private String pinCreatedBy;
    @Column(name = "pin_modification_date")
    private LocalDateTime pinModificationDate;
    @Column(name = "pin_modified_by", length = UdbConstants.MAX_LENGTH_FIFTY)
    private String pinModifiedBy;
}

