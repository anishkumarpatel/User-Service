package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.dto.request.CountryRequest;
import com.unisys.udb.user.dto.response.RuleEngineResponse;
import com.unisys.udb.user.exception.RuleEngineIntegrationException;
import com.unisys.udb.user.service.RuleEngineService;
import com.unisys.udb.user.service.client.RuleEngineServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@Slf4j
@RequiredArgsConstructor
public class RuleEngineServiceImpl implements RuleEngineService {

    private final RuleEngineServiceClient ruleEngineServiceClient;

    @Override
    public RuleEngineResponse getEvidenceDocumentDetails(CountryRequest countryRequest) {
        RuleEngineResponse ruleEngineResponse = null;
        try {
            ruleEngineResponse = ruleEngineServiceClient.getEvidenceDocumentDetails(countryRequest);
        } catch (WebClientResponseException exception) {
            log.error(ExceptionUtils.getStackTrace(exception));
            throw new RuleEngineIntegrationException(exception.getResponseBodyAsString());
        } catch (Exception exception) {
            log.error(ExceptionUtils.getStackTrace(exception));
            throw new RuleEngineIntegrationException(exception.getMessage());
        }
        return ruleEngineResponse;
    }
}