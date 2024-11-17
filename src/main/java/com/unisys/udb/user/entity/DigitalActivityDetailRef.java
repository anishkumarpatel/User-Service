package com.unisys.udb.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "digital_activity_detail_ref", schema = "digital")
public class DigitalActivityDetailRef {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "digital_activity_detail_ref_id")
    private Integer digitalActivityDetailRefId;

    @Column(name = "digital_activity_name")
    private String digitalActivityName;

    @Column(name = "activity_creation_date")
    private LocalDateTime activityCreationDate;

    @Column(name = "activity_created_by")
    private String activityCreatedBy;

    @Column(name = "activity_modification_date")
    private LocalDateTime activityModificationDate;

    @Column(name = "activity_modified_by")
    private String activityModifiedBy;
}
