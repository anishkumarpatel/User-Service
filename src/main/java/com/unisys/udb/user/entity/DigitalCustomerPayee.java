package com.unisys.udb.user.entity;

import com.unisys.udb.user.constants.SchemaConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "digital_customer_payee", schema = SchemaConstants.DIGITAL)
public class DigitalCustomerPayee {
    @Column(name = "digital_customer_payee_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer digitalCustomerPayeeId;

    @Column(name = "digital_customer_profile_id", nullable = false)
    private UUID digitalCustomerProfileId;

    @Column(name = "payee_name")
    private String payeeName;

    @Column(name = "payee_bank_name")
    private String payeeBankName;

    @Column(name = "payee_account_number")
    private String payeeAccountNumber;

    @Column(name = "payee_nickname")
    private String payeeNickName;

    @Column(name = "payee_bank_code")
    private String payeeBankCode;

    @Column(name = "payee_reference")
    private String payeeReference;

    @Column(name = "payee_creation_date")
    private LocalDateTime payeeCreationDate;

    @Column(name = "payee_created_by")
    private String payeeCreatedBy;

    @Column(name = "payee_modification_date")
    private LocalDateTime payeeModificationDate;

    @Column(name = "payee_modified_by")
    private String payeeModifiedBy;
}