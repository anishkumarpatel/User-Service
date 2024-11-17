package com.unisys.udb.user.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "digital_customer_activity", schema = "dbo")
public class DigitalCustomerActivity {
    @Column(name = "digital_customer_profile_id")
    @Id
    private UUID digitalCustomerProfileId;
    @Column(name = "activity_status")
    private String activityStatus;
    @Column(name = "activity_time")
    private LocalDate activityTime;
    @Column(name = "activity_channel")
    private LocalDate activityChannel;


}
