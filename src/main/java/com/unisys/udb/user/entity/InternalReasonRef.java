package com.unisys.udb.user.entity;

import com.unisys.udb.user.constants.SchemaConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "internal_reason_ref", schema = SchemaConstants.INTERNAL)
public class InternalReasonRef {
    @Column(name = "internal_reason_ref_id", nullable = false)
    @Id
    private Integer internalReasonRefId;
    @Column(name = "reason_category")
    private String reasonCategory;
    @Column(name = "reason_name")
    private String reasonName;
    @Column(name = "enabled_flag")
    private Boolean enabledFlag;
    @Column(name = "reason_creation_date")
    private LocalDateTime reasonCreationDate;
    @Column(name = "reason_modification_date")
    private LocalDateTime reasonModificationDate;
    @Column(name = "reason_created_by")
    private String reasonCreatedBy;
    @Column(name = "reason_modified_by")
    private String reasonModifiedBy;
}
