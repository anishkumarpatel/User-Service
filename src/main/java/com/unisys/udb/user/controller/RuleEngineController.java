package com.unisys.udb.user.controller;

import com.unisys.udb.user.dto.request.CountryRequest;
import com.unisys.udb.user.dto.response.RuleEngineResponse;
import com.unisys.udb.user.service.RuleEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/ruleEngine")
@Slf4j
@RequiredArgsConstructor
public class RuleEngineController {

    private final RuleEngineService ruleEngineService;

    @GetMapping(path = "/evidenceDocument/{country}")
    public Mono<ResponseEntity<RuleEngineResponse>> getEvidenceDocumentDetails(@PathVariable String country) {

        CountryRequest countryRequest = new CountryRequest();
        countryRequest.setCountry(country);

        RuleEngineResponse ruleEngineResponse = ruleEngineService.getEvidenceDocumentDetails(countryRequest);
        return Mono.just(new ResponseEntity<>(ruleEngineResponse, HttpStatus.OK));
    }
}