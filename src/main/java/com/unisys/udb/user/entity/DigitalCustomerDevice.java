package com.unisys.udb.user.entity;


import com.unisys.udb.user.constants.SchemaConstants;
import com.unisys.udb.user.constants.UdbConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "digital_customer_device", schema = SchemaConstants.DIGITAL)
public class DigitalCustomerDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "digital_customer_device_id", nullable = false)
    private Integer digitalCustomerDeviceId;

    @Column(name = "digital_device_udid", length = UdbConstants.MAX_LENGTH_FIFTY, nullable = false)
    private String digitalDeviceUdid;

    @Column(name = "device_name", length = UdbConstants.MAX_LENGTH_FIFTY, nullable = false)
    private String deviceName;

    @Column(name = "device_type", length = UdbConstants.MAX_LENGTH_THIRTY, nullable = false)
    private String deviceType;

    @Column(name = "device_os_version", length = UdbConstants.MAX_LENGTH_100, nullable = false)
    private String deviceOsVersion;

    @Column(name = "device_token", length = UdbConstants.MAX_LENGTH_250)
    private String deviceToken;

    @Column(name = "device_enabled_flag", nullable = false)
    private Boolean deviceStatus;

    @Column(name = "device_creation_date", nullable = false)
    private LocalDateTime deviceCreationDate;

    @Column(name = "device_created_by", nullable = false, length = UdbConstants.MAX_LENGTH_FIFTY)
    private String deviceCreatedBy;

    @Column(name = "device_modification_date")
    private LocalDateTime deviceModificationDate;

    @Column(name = "device_modification_by", length = UdbConstants.MAX_LENGTH_FIFTY)
    private String deviceModifiedBy;

    @Column(name = "terms_conditions", nullable = false)
    private Boolean termsAndConditions;

    @Column(name = "strictly_acceptance_cookie", nullable = false)
    private Boolean strictlyAcceptanceCookie;

    @Column(name = "performance_cookie", nullable = false)
    private Boolean performanceCookie;

    @Column(name = "functional_cookie", nullable = false)
    private Boolean functionalCookie;

    @OneToOne(mappedBy = "digitalCustomerDevice")
    private DigitalDeviceLink digitalDeviceLink;

    @Column(name = "device_face_auth_public_key")
    private String deviceFacePublicKey;

    @Column(name = "device_touch_auth_public_key")
    private String deviceTouchPublicKey;

    @Column(name = "device_pin_auth_public_key")
    private String devicePinPublicKey;
}
