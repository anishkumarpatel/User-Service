package com.unisys.udb.user.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "digital_marketing_template")
public class MarketingNotificationTemplate {
    @Id
    @Field("_id")
    private String id;

    @Field("message_code")
    private String messageCode;

    @Field("message_content")
    private Map<String, String> messageContent;

    @Field("file_content")
    private String fileContent;

    @Field("created_at")
    private String createdAt;

    @Field("created_by")
    private String createdBy;

    @Field("updated_at")
    private String updatedAt;

    @Field("updated_by")
    private String updatedBy;
}
