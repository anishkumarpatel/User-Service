package com.unisys.udb.user.controller;

import com.unisys.udb.user.dto.response.RuleEngineResponse;
import com.unisys.udb.user.service.RuleEngineService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RuleEngineControllerTest {
    private final int okStatus = 200;

    @Test
    void testGetEvidenceDocumentDetails() throws AssertionError {
        RuleEngineService ruleEngineService = mock(RuleEngineService.class);
        when(ruleEngineService.getEvidenceDocumentDetails(Mockito.any())).thenReturn(new RuleEngineResponse());

        StepVerifier.FirstStep<ResponseEntity<RuleEngineResponse>> createResult = StepVerifier
                .create((new RuleEngineController(ruleEngineService)).getEvidenceDocumentDetails("GB"));
        createResult.assertNext(r -> {
            ResponseEntity<RuleEngineResponse> responseEntity = r;
            RuleEngineResponse body = responseEntity.getBody();
            body.getCountry();
            body.getEvidenceRequired();
            body.getUrl();
            assertTrue(responseEntity.getHeaders().isEmpty());
            responseEntity.getStatusCode();
            assertEquals(okStatus, responseEntity.getStatusCodeValue());
            assertTrue(responseEntity.hasBody());
            return;
        }).expectComplete().verify();
        verify(ruleEngineService).getEvidenceDocumentDetails(any());
    }
}
