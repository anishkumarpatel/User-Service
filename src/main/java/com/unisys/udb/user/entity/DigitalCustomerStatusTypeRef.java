package com.unisys.udb.user.entity;

import com.unisys.udb.user.constants.SchemaConstants;
import com.unisys.udb.user.constants.UdbConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "digital_customer_status_type_ref", schema = SchemaConstants.DIGITAL)
public class DigitalCustomerStatusTypeRef {
    @Column(name = "digital_customer_status_type_ref_id", nullable = false)
    @Id
    private Integer digitalCustomerStatusTypeRefId;
    @Column(name = "customer_status_type", nullable = false, unique = true)
    private String customerStatusType;
    @Column(name = "status_description", nullable = false)
    private String statusDescription;
    @Column(name = "status_type_creation_date")
    private LocalDateTime statusTypeCreationDate;
    @Column(name = "status_type_modification_date")
    private LocalDateTime statusTypeModificationDate;
    @Column(name = "status_type_created_by", length = UdbConstants.MAX_LENGTH_FIFTY)
    private String statusTypeCreatedBy;
    @Column(name = "status_type_modified_by", length = UdbConstants.MAX_LENGTH_FIFTY)
    private String statusTypeModifiedBy;
}
