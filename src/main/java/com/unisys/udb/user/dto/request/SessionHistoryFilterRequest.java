package com.unisys.udb.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionHistoryFilterRequest {
    @NotEmpty(message = "Channel map cannot be empty")
    private Map<String, Boolean> byChannel;
    @NotEmpty(message = "Activity map cannot be empty")
    private Map<String, Boolean> byActivity;
    @NotEmpty(message = "Date map cannot be empty")
    private Map<String, String> byDate;
}
