package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.request.CountryRequest;
import com.unisys.udb.user.dto.response.RuleEngineResponse;

public interface RuleEngineService {

    RuleEngineResponse getEvidenceDocumentDetails(CountryRequest countryRequest);
}