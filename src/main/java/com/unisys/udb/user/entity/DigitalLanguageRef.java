package com.unisys.udb.user.entity;

import com.unisys.udb.user.constants.SchemaConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "digital_language_ref", schema = SchemaConstants.DIGITAL)
public class DigitalLanguageRef {
    @Id
    @Column(name = "digital_language_ref_id")
    private int digitalLanguageRefId;

    @Column(name = "locale_code")
    private String localeCode;

    @Column(name = "language_name")
    private String languageName;

    @Column(name = "language_creation_date")
    private LocalDateTime languageCreationDate;

    @Column(name = "language_modification_date")
    private LocalDateTime languageModificationDate;

    @Column(name = "language_created_by")
    private String languageCreatedBy;

    @Column(name = "language_modified_by")
    private String languageModifiedBy;

    @Column(name = "language_enabled_flag")
    private Boolean languageEnabledFlag;

    @Column(name = "language_img_path")
    private String languageImgPath;

    @Column(name = "template_img_path")
    private String templateImgPath;
}
