package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.response.PinHistoryResponse;
import com.unisys.udb.user.exception.PinHistoryRetrievalException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component

public interface PinHistoryService {

    PinHistoryResponse fetchOldPins(UUID digitalCustomerProfileId) throws PinHistoryRetrievalException;

}

