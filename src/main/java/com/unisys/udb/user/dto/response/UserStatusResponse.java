package com.unisys.udb.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusResponse {
    private String accountStatus;
    private boolean isPasswordExpired;
    private boolean isPinExpired;
}
