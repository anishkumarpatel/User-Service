package com.unisys.udb.user.entity;

import com.unisys.udb.user.constants.UdbConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "digital_customer_activity", schema = "digital")
public class DigitalCustomerActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "digital_customer_activity_id", nullable = false)
    private int digitalCustomerActivityId;

    @Column(name = "digital_customer_profile_id", nullable = false)
    private UUID digitalCustomerProfileId;

    @Column(name = "digital_customer_device_id", nullable = false)
    private int digitalCustomerDeviceId;

    @Column(name = "digital_activity_detail_ref_id", nullable = false)
    private int digitalActivityDetailRefId;

    @Column(name = "activity_status", length = UdbConstants.MAX_LENGTH_100)
    private String activityStatus;

    @Column(name = "activity_time")
    private LocalDateTime activityTime;

    @Column(name = "activity_channel", length = UdbConstants.MAX_LENGTH_100)
    private String activityChannel;

    @Column(name = "activity_corelation_key", nullable = false)
    private UUID activityCorelationKey;

}
