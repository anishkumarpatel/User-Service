package com.unisys.udb.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastMessageContentRequest {

    @NotNull(message = "Message id must not be null")
    private Long messageId;

    @NotEmpty(message = "Broadcast message content must not be empty")
    private List<BroadcastMessageContent> brodCastMessageContent;

    private String createdBy;

}
