package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.request.PinChangeRequest;
import com.unisys.udb.user.dto.response.ComparePinResponse;
import com.unisys.udb.user.dto.response.PinChangeResponse;
import com.unisys.udb.user.exception.PinValidationException;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public interface PinService {

    PinChangeResponse validatePinHistory(PinChangeRequest pinChangeRequest) throws PinValidationException;

    PinChangeResponse saveOldPin(PinChangeRequest pinChangeRequest) throws PinValidationException;

    ComparePinResponse comparePin(UUID digitalCustomerProfileId, String newPin);

}
