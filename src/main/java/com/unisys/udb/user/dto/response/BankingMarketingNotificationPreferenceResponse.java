package com.unisys.udb.user.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankingMarketingNotificationPreferenceResponse {
    private BankingNotificationPreferenceResponse bankingNotificationPreference;
    private MarketingNotificationPreferenceResponse marketingNotificationPreference;
}
