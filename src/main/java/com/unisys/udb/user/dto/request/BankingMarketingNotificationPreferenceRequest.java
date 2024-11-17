package com.unisys.udb.user.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankingMarketingNotificationPreferenceRequest {

    private BankingNotificationPreferenceRequest bankingRequest;
    private MarketingNotificationPreferenceRequest marketingRequest;
}
