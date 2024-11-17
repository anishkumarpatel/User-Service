package com.unisys.udb.user.entity.cah;


import com.unisys.udb.user.constants.SchemaConstants;
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

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "digital_docdb_broadcast_msg_ref", schema = SchemaConstants.DIGITAL)
public class BroadcastMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "digital_docdb_broadcast_msg_ref_id", nullable = false)
    private Long messageId;

    @Column(name = "broadcast_message_name", nullable = false)
    private String messageName;

    @Column(name = "broadcast_message_start_date", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "broadcast_message_end_date", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "broadcast_to_all", nullable = false)
    private boolean accountType;

    @Column(name = "broadcast_message_creation_date")
    private LocalDateTime creationDate;

    @Column(name = "broadcast_message_created_by")
    private String createdBy;

    @Column(name = "broadcast_message_modification_date")
    private LocalDateTime modificationDate;

    @Column(name = "broadcast_message_modified_by")
    private String modifiedBy;

    @Column(name = "digital_template_status_ref_id")
    private Integer templateStatusRefId;
}
