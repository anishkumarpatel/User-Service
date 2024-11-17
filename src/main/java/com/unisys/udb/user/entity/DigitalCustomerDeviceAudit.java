package com.unisys.udb.user.entity;

import com.unisys.udb.user.constants.SchemaConstants;
import com.unisys.udb.user.constants.UdbConstants;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "digital_device_link_audit", schema = SchemaConstants.DIGITAL)
public class DigitalCustomerDeviceAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "digital_device_link_audit_id", nullable = false)
    private Integer digitalDeviceLinkAuditId;

    @OneToOne
    @JoinColumn(name = "digital_customer_profile_id", referencedColumnName = "digital_customer_profile_id",
            nullable = false)
    private DigitalCustomerProfile digitalCustomerProfile;

    @OneToOne
    @JoinColumn(name = "digital_customer_device_id", referencedColumnName = "digital_customer_device_id",
            nullable = false)
    private DigitalCustomerDevice digitalCustomerDevice;

    @Column(name = "device_link_registered_flag_audit", nullable = false)
    private Boolean deviceLinkRegisteredFlagAudit;

    @Column(name = "digital_audit_type_ref_id", nullable = false)
    private Integer deviceAuditTypeRefId;

    @ManyToOne
    @JoinColumn(name = "digital_device_link_id", nullable = false)
    private DigitalDeviceLink digitalDeviceLink;

    @Column(name = "device_link_audit_creation_date")
    private LocalDateTime deviceLinkAuditCreationDate;

    @Column(name = "device_link_audit_created_by", length = UdbConstants.MAX_LENGTH_FIFTY)
    private String deviceLinkAuditCreatedBy;
}
