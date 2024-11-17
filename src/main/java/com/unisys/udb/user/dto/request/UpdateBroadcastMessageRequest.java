package com.unisys.udb.user.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBroadcastMessageRequest {
    @NotNull(message = "Message ID cannot be null")
    private Integer messageId;

    @NotEmpty(message = "Account type cannot be empty")
    private String accountType;

    @NotEmpty(message = "Message name cannot be empty")
    private String messageName;

    @NotEmpty(message = "Start date and time cannot be empty")
    @Pattern(regexp = "\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}",
            message = "Start date and time must follow the format dd-MM-yyyy HH:mm:ss")
    private String startDateTime;

    @NotEmpty(message = "End date and time cannot be empty")
    @Pattern(regexp = "\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}",
            message = "End date and time must follow the format dd-MM-yyyy HH:mm:ss")
    private String endDateTime;

    @NotEmpty(message = "Updated by cannot be empty")
    private String updatedBy;

    @Valid
    @NotEmpty(message = "Broadcast message content cannot be empty")
    private List<BroadcastMessageContent> brodCastMessageContent;
}