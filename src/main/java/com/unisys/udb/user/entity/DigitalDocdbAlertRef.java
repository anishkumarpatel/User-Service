package com.unisys.udb.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "digital_docdb_alert_ref", schema = "digital")
public class DigitalDocdbAlertRef {

    @Id
    @Column(name = "digital_docdb_alert_ref_id")
    private int digitalDocDbAlertRefId;

    @Column(name = "digital_alert_key")
    private String digitalAlertKey;

    @Column(name = "key_creation_date")
    private LocalDateTime keyCreationDate;

    @Column(name = "key_created_by")
    private String keyCreatedBy;

    @Column(name = "key_modification_date")
    private LocalDateTime keyModificationDate;

    @Column(name = "key_modified_by")
    private String keyModifiedBy;
}
