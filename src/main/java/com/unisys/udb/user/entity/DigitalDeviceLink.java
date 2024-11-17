package com.unisys.udb.user.entity;

import com.unisys.udb.user.constants.SchemaConstants;
import com.unisys.udb.user.constants.UdbConstants;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "digital_device_link", schema = SchemaConstants.DIGITAL)
public class DigitalDeviceLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "digital_device_link_id")
    private Integer digitalDeviceLinkId;

    @OneToOne
    @JoinColumn(name = "digital_customer_device_id", referencedColumnName = "digital_customer_device_id")
    private DigitalCustomerDevice digitalCustomerDevice;

    @ManyToOne
    @JoinColumn(name = "digital_customer_profile_id", referencedColumnName = "digital_customer_profile_id")
    private DigitalCustomerProfile digitalCustomerProfile;

    @Column(name = "device_link_creation_date", nullable = false)
    private LocalDateTime deviceLinkCreationDate;
    @Column(name = "device_link_created_by", length = UdbConstants.MAX_LENGTH_FIFTY)
    private String deviceLinkCreatedBy;
    @Column(name = "device_link_modification_date")
    private LocalDateTime deviceLinkModificationDate;
    @Column(name = "device_link_modified_by", length = UdbConstants.MAX_LENGTH_FIFTY)
    private String deviceLinkModifiedBy;
    @Column(name = "device_link_registered_flag")
    private Boolean deviceLinkRegisterFlag;
    @OneToMany(mappedBy = "digitalDeviceLink")
    private List<DigitalCustomerDeviceAudit> digitalCustomerDeviceAudit;

}