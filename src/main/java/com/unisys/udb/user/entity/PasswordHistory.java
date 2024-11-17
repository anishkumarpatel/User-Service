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

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "digital_customer_pwd", schema = "digital")
public class PasswordHistory {

    @Id
    @Column(name = "digital_customer_pwd_id")
    private Long id;

    @Column(name = "digital_customer_profile_id")
    private Long digitalCustomerProfileId;

    @Column(name = "old_pwd")
    private String oldPassword;

    @Column(name = "pwd_change_date")
    private LocalDateTime pwdChangeDate;

    @Column(name = "pwd_expiry_date")
    private LocalDateTime pwdExpiryDate;

    @Column(name = "pwd_creation_date")
    private LocalDateTime pwdCreationDate;

    @Column(name = "pwd_created_by")
    private String pwdCreatedBy;

    @Column(name = "pwd_modification_date")
    private LocalDateTime pwdModificationDate;

    @Column(name = "pwd_modified_by")
    private String pwdModifiedBy;

}