package com.unisys.udb.user.entity;

import com.unisys.udb.user.constants.UdbConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "digital_customer_shortcut", schema = "digital")
public class DigitalCustomerShortcuts {

    @Column(name = "digital_customer_shortcut_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long digitalCustomerShortcutsId;
    @Column(name = "digital_customer_profile_id", updatable = false, nullable = false)
    private UUID digitalCustomerProfileId;
    @Column(name = "fund_transfer_shortcut", nullable = false)
    private Boolean fundTransferShortcut;
    @Column(name = "estatement_shortcut", nullable = false)
    private Boolean estatementShortcut;
    @Column(name = "payee_shortcut", nullable = false)
    private Boolean payeeShortcut;
    @Column(name = "scheduled_payments_shortcut", nullable = false)
    private Boolean scheduledPaymentsShortcut;
    @Column(name = "cmnctn_preference_shortcut", nullable = false)
    private Boolean cmnctnPreferenceShortcut;
    @Column(name = "session_history_shortcut", nullable = false)
    @NotNull
    private Boolean sessionHistoryShortcut;
    @Column(name = "shortcut_creation_date", updatable = false)
    @CreatedDate
    private LocalDateTime shortcutCreationDate;
    @Column(name = "shortcut_created_by", updatable = false, length = UdbConstants.MAX_LENGTH_THIRTY)
    @CreatedBy
    private String shortcutCreatedBy;
    @Column(name = "shortcut_modification_date")
    @LastModifiedDate
    private LocalDateTime shortcutModificationDate;
    @Column(name = "shortcut_modified_by", length = UdbConstants.MAX_LENGTH_THIRTY)
    @LastModifiedBy
    private String shortcutModifiedBy;

}