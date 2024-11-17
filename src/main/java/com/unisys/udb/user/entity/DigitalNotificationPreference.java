package com.unisys.udb.user.entity;

import com.unisys.udb.user.constants.SchemaConstants;
import com.unisys.udb.user.constants.UdbConstants;
import jakarta.persistence.*;
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
@Table(name = "digital_notification_preference", schema = SchemaConstants.DIGITAL)
public class DigitalNotificationPreference {
    @Column(name = "digital_notification_preference_id", nullable = false, length = UdbConstants.MAX_LENGTH_FIFTY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer digitalBankingNotificationPreferenceId;
    @Column(name = "digital_customer_profile_id", nullable = false)
    private UUID digitalCustomerProfileId;
    @Column(name = "mobile_push_notification", nullable = false)
    private boolean mobilePushNotificationBanking;
    @Column(name = "email_notification", nullable = false)
    private boolean emailNotificationBanking;
    @Column(name = "sms_notification", nullable = false)
    private boolean smsNotificationBanking;
    @Column(name = "notification_creation_date", nullable = false)
    private LocalDateTime notificationCreationDate;
    @Column(name = "notification_created_by", nullable = false, length = UdbConstants.MAX_LENGTH_FIFTY)
    private String notificationCreatedBy;
    @Column(name = "notification_modification_date")
    private LocalDateTime notificationModificationDate;
    @Column(name = "notification_modified_by", length = UdbConstants.MAX_LENGTH_FIFTY)
    private String notificationModifiedBy;


}
