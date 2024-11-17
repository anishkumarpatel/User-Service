package com.unisys.udb.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "digital_customer_pwd", schema = "digital")
public class PinHistory {

    @Id
    @Column(name = "digital_customer_pin_id")
    private Long id;

    @Column(name = "digital_customer_profile_id")
    private UUID digitalCustomerProfileId;

    @Column(name = "old_pin")
    private String oldPin;

    @Column(name = "pin_change_date")
    private LocalDateTime pinChangeDate;

    @Column(name = "pin_expiry_date")
    private LocalDateTime pinExpiryDate;

    @Column(name = "pin_creation_date")
    private LocalDateTime pinCreationDate;

    @Column(name = "pin_created_by")
    private String pinCreatedBy;

    @Column(name = "pin_modification_date")
    private LocalDateTime pinModificationDate;

    @Column(name = "pin_modified_by")
    private String pinModifiedBy;

}