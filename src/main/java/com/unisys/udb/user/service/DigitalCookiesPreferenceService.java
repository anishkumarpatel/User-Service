package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.request.DigitalCookiesPreferenceRequest;
import com.unisys.udb.user.dto.response.DigitalCookiesPreferenceResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DigitalCookiesPreferenceService {

    Mono<DigitalCookiesPreferenceResponse> saveDigitalCookiesPreferences(
            UUID digitalCustomerProfileId, DigitalCookiesPreferenceRequest cookiesPreferenceRequest);
}
