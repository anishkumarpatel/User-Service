package com.unisys.udb.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DigitalCustomerShortcutsResponse {
    private Boolean fundTransferShortcut;
    private Boolean eStatementShortcut;
    private Boolean payeeShortcut;
    private Boolean scheduledPaymentsShortcut;
    private Boolean commPrefShortcut;
    private Boolean sessionHistoryShortcut;
}
