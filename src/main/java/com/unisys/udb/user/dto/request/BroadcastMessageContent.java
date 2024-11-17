package com.unisys.udb.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BroadcastMessageContent {
    @NotEmpty(message = "Locale code cannot be empty")
    private String localeCode;

    @NotEmpty(message = "Message content cannot be empty")
    private String messageContent;
}
