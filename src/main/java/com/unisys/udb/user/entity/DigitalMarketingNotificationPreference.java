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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "digital_marketing_notification_preference", schema = SchemaConstants.DIGITAL)
public class DigitalMarketingNotificationPreference {
    @Column(name = "digital_marketing_notification_preference_id", nullable = false, length =
            UdbConstants.MAX_LENGTH_FIFTY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer digitalMarketingNotificationPreferenceId;
    @Column(name = "digital_customer_profile_id", nullable = false)
    private UUID digitalCustomerProfileId;
    @Column(name = "marketing_email_notification", nullable = false)
    private boolean marketingEmailNotification;
    @Column(name = "marketing_sms_notification", nullable = false)
    private boolean marketingSmsNotification;
    @Column(name = "marketing_post_notification", nullable = false)
    private boolean marketingPostNotification;
    @Column(name = "marketing_telephone_notification", nullable = false)
    private boolean marketingTelephoneNotification;
    @Column(name = "marketing_online_notification", nullable = false)
    private boolean marketingOnlineNotification;
    @Column(name = "notification_creation_date", nullable = false)
    private LocalDateTime notificationCreationDate;
    @Column(name = "notification_created_by", nullable = false, length = UdbConstants.MAX_LENGTH_FIFTY)
    private String notificationCreatedBy;
    @Column(name = "notification_modification_date")
    private LocalDateTime notificationModificationDate;
    @Column(name = "notification_modified_by", length = UdbConstants.MAX_LENGTH_FIFTY)
    private String notificationModifiedBy;
}
