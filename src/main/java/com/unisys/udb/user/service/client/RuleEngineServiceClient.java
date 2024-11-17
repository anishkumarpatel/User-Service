package com.unisys.udb.user.service.client;

import com.unisys.udb.user.dto.request.CountryRequest;
import com.unisys.udb.user.dto.response.RuleEngineResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface RuleEngineServiceClient {
    @PostExchange("/api/v1/rule/executeRule/CountryValidation")
    RuleEngineResponse getEvidenceDocumentDetails(@RequestBody CountryRequest countryRequest)
            throws WebClientResponseException;
}
