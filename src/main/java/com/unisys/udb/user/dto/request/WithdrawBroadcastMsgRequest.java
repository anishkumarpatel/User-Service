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
public class WithdrawBroadcastMsgRequest {

    @NotNull(message = "Message IDs list cannot be null")
    @NotEmpty(message = "Message IDs list cannot be empty")
    private List<Integer> messageIds;
    private String modifiedBy;

}
