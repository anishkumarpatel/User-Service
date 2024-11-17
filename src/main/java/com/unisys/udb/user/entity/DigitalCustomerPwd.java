package com.unisys.udb.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "digital_customer_pwd", schema = "digital")
public class DigitalCustomerPwd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "digital_customer_pwd_id")
    private Long id;

    @Column(name = "digital_customer_profile_id", nullable = false)
    private UUID digitalCustomerProfileId;

    @Column(name = "old_pwd", nullable = false)
    private String encryptedOldPassword;

    @Column(name = "pwd_change_date")
    private Date passwordChangeDate;

    @Column(name = "pwd_expiry_date")
    private Date passwordExpiryDate;

    @Column(name = "pwd_creation_date")
    private Date passwordCreationDate;

    @Column(name = "pwd_created_by")
    private String passwordCreatedBy;

    @Column(name = "pwd_modification_date")
    private Date passwordModificationDate;

    @Column(name = "pwd_modified_by")
    private String passwordModifiedBy;
}
