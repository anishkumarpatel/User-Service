package com.unisys.udb.user.config;

import com.unisys.udb.user.service.client.BrandingServiceClient;
import com.unisys.udb.user.service.client.ConfigurationServiceClient;
import com.unisys.udb.user.service.client.MFAServiceClient;
import com.unisys.udb.user.service.client.NotificationOrchestratorServiceClient;
import com.unisys.udb.user.service.client.RuleEngineServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfig {

    @Value("${notification.orchestrator.service.url}")
    private String notificationOrchestratorServiceUrl;

    @Value("${web-client.configuration.service}")
    private String configurationServiceBaseUrl;
    @Value("${web-client.rule.service.url}")
    private String ruleEngineServiceBaseUrl;

    @Value("${web-client.branding.service.url}")
    private String brandingServiceBaseUrl;

    @Value("${web-client.mfa.service.url}")
    private String mfaUrl;

    @Bean
    public WebClient notificationOrchestratorWebClient() {
        return WebClient.builder().baseUrl(notificationOrchestratorServiceUrl).build();
    }

    @Bean
    public NotificationOrchestratorServiceClient notificationOrchestratorServiceClient() {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(notificationOrchestratorWebClient())).build();
        return httpServiceProxyFactory.createClient(NotificationOrchestratorServiceClient.class);
    }

    @Bean
    public WebClient configurationServiceWebClient() {
        return WebClient.builder().baseUrl(configurationServiceBaseUrl).build();
    }

    @Bean
    public ConfigurationServiceClient configurationClient() {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(configurationServiceWebClient())).build();
        return httpServiceProxyFactory.createClient(ConfigurationServiceClient.class);
    }

    @Bean
    public WebClient ruleEngineServiceWebClient() {
        return WebClient.builder().baseUrl(ruleEngineServiceBaseUrl).build();
    }

    @Bean
    public RuleEngineServiceClient ruleEngineClient() {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(ruleEngineServiceWebClient())).build();
        return httpServiceProxyFactory.createClient(RuleEngineServiceClient.class);
    }

    @Bean
    public WebClient brandingServiceWebClient() {
        return WebClient.builder().baseUrl(brandingServiceBaseUrl).build();
    }

    @Bean
    public BrandingServiceClient brandingServiceClient() {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(brandingServiceWebClient())).build();
        return httpServiceProxyFactory.createClient(BrandingServiceClient.class);
    }

    @Bean
    public WebClient mfaServiceWebClient() {
        return WebClient.builder().baseUrl(mfaUrl).build();
    }

    @Bean
    public MFAServiceClient mfaClient() {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(mfaServiceWebClient())).build();
        return httpServiceProxyFactory.createClient(MFAServiceClient.class);
    }
}