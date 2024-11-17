package com.unisys.udb.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "digital_customer_alert", schema = "digital")
public class DigitalCustomerAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "digital_customer_alert_id")
    private int digitalCustomerAlertId;

    @Column(name = "digital_customer_profile_id")
    private UUID digitalCustomerProfileId;

    @Column(name = "alert_read_flag")
    private boolean alertReadFlag;

    @Column(name = "alert_creation_date")
    private LocalDateTime alertCreationDate;

    @Column(name = "alert_created_by")
    private String alertCreatedBy;

    @Column(name = "alert_modification_date")
    private LocalDateTime alertModificationDate;

    @Column(name = "alert_modified_by")
    private String alertModifiedBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "digital_docdb_alert_ref_id", referencedColumnName = "digital_docdb_alert_ref_id")
    private DigitalDocdbAlertRef digitalDocdbAlertRef;
}
