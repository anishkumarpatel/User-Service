package com.unisys.udb.user.service.client;

import com.unisys.udb.user.dto.response.DeviceRegistrationLimitResponse;
import com.unisys.udb.user.dto.response.GlobalConfigResponse;
import com.unisys.udb.user.dto.response.MarketingPreferenceResponse;
import com.unisys.udb.user.dto.response.NotificationPreferenceResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange
public interface ConfigurationServiceClient {

    @GetExchange("/api/v1/configuration/marketing/preferences")
    List<MarketingPreferenceResponse> getDefaultMarketingPreferences() throws WebClientResponseException;

    @GetExchange("/api/v1/configuration/notification/preferences")
    List<NotificationPreferenceResponse> getDefaultNotificationPreferences() throws WebClientResponseException;

    @GetExchange("/api/v1/configuration/registered/devices/limit")
    DeviceRegistrationLimitResponse getDeviceRegistrationMaxLimit() throws WebClientResponseException;

    @GetExchange("api/v1/configuration/global-config/values")
    List<GlobalConfigResponse> getGlobalConfig(
            @RequestParam String globalConfigCode);

}
