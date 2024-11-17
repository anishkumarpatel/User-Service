package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.dto.response.RuleEngineResponse;
import com.unisys.udb.user.exception.RuleEngineIntegrationException;
import com.unisys.udb.user.service.client.RuleEngineServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RuleEngineServiceImpl.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class RuleEngineServiceImplTest {
    @MockBean
    private RuleEngineServiceClient ruleEngineServiceClient;

    @Autowired
    private RuleEngineServiceImpl ruleEngineServiceImpl;

    @Test
    void testGetEvidenceDocumentDetails() throws WebClientResponseException {

        RuleEngineResponse ruleEngineResponse = new RuleEngineResponse();
        when(ruleEngineServiceClient.getEvidenceDocumentDetails(Mockito.any())).thenReturn(ruleEngineResponse);

        RuleEngineResponse actualEvidenceDocumentDetails =
                ruleEngineServiceImpl.getEvidenceDocumentDetails(any());

        verify(ruleEngineServiceClient).getEvidenceDocumentDetails(any());
        assertSame(ruleEngineResponse, actualEvidenceDocumentDetails);
    }

    @Test
    void testGetEvidenceDocumentDetails2() throws WebClientResponseException {

        when(ruleEngineServiceClient.getEvidenceDocumentDetails(Mockito.any()))
                .thenThrow(new RuleEngineIntegrationException("An error occurred"));

        assertThrows(RuleEngineIntegrationException.class, () -> {
            throw new RuleEngineIntegrationException("Error while getting evidence document details");
        });
    }

}
