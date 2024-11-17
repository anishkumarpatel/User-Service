package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.request.PinChangeRequest;
import com.unisys.udb.user.dto.response.ComparePinResponse;
import com.unisys.udb.user.dto.response.PinChangeResponse;
import com.unisys.udb.user.entity.DigitalCustomerPin;
import com.unisys.udb.user.exception.DigitalPinStorageException;
import com.unisys.udb.user.exception.PinHistoryRetrievalException;
import com.unisys.udb.user.exception.PinValidationException;
import com.unisys.udb.user.repository.PinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.unisys.udb.user.constants.UdbConstants.ERROR_SAVING_PIN_DETAILS;
import static com.unisys.udb.user.constants.UdbConstants.EXCEPTION;
import static com.unisys.udb.user.constants.UdbConstants.PIN_MATCHED;
import static com.unisys.udb.user.constants.UdbConstants.PIN_NOT_MATCHED;

@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class PinServiceImpl implements PinService {
    private final PinRepository pinRepository;
    @Override
    public PinChangeResponse validatePinHistory(PinChangeRequest pinChangeRequest) throws PinValidationException {
        log.info("pinChangeRequest for: {}", pinChangeRequest.getUserName());
        PinChangeResponse pinChangeResponse = new PinChangeResponse();
        if (pinChangeRequest.getOldPin().equals(pinChangeRequest.getNewPin())) {
            pinChangeResponse.setStatusCode(HttpStatus.NOT_MODIFIED.value());
            pinChangeResponse.setMessage("SHOULD_NOT_BE_SAME");
            return pinChangeResponse;
        }
        long matchCount = 0;
        try {
            List<DigitalCustomerPin> pinHistoryList = pinRepository.getPinHistory(
                    pinChangeRequest.getDigitalCustomerProfileId());
            matchCount = pinHistoryList.stream().filter(pinHistoryListObj ->
                    BCrypt.checkpw(pinChangeRequest.getNewPin(),
                            pinHistoryListObj.getOldPin())
            ).count();

        } catch (Exception e) {
            pinChangeResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.ordinal());
            log.error("getPinHistory" + EXCEPTION, ExceptionUtils.getStackTrace(e));
        }
        if (matchCount >= 1) {
            pinChangeResponse.setStatusCode(HttpStatus.NOT_MODIFIED.value());
            pinChangeResponse.setMessage("SHOULD_NOT_SAME_AS_PREVIOUS_PIN");
            return pinChangeResponse;
        }
        pinChangeResponse.setStatusCode(HttpStatus.OK.value());
        return pinChangeResponse;
    }

    @Override
    public PinChangeResponse saveOldPin(PinChangeRequest pinChangeRequest) {
        PinChangeResponse pinChangeResponse = new PinChangeResponse();
        try {
            DigitalCustomerPin digitalCustomerPin = new DigitalCustomerPin();
            digitalCustomerPin.setDigitalCustomerProfileId(pinChangeRequest.getDigitalCustomerProfileId());

            String encryptedExistingPin = BCrypt.hashpw(pinChangeRequest.getOldPin(), BCrypt.gensalt());
            // Set the XmlField parameter in the prepared statement.
            digitalCustomerPin.setOldPin(encryptedExistingPin);
            digitalCustomerPin.setPinChangeDate(LocalDateTime.now());
            digitalCustomerPin.setPinExpiryDate(LocalDateTime.now());
            digitalCustomerPin.setPinCreatedBy(pinChangeRequest.getUserName());
            digitalCustomerPin.setPinCreationDate(LocalDateTime.now());
            digitalCustomerPin.setPinModifiedBy(pinChangeRequest.getUserName());
            digitalCustomerPin.setPinModificationDate(LocalDateTime.now());
            pinRepository.save(digitalCustomerPin);
            pinChangeResponse.setStatusCode(HttpStatus.OK.value());
            pinChangeResponse.setMessage("COMPLETED");
            log.info("PinChangeRequest saved successfully: {}", pinChangeRequest.toString());

        } catch (Exception e) {
            log.error("saveOldPin" + EXCEPTION, ExceptionUtils.getStackTrace(e));
            log.error("Error in saveOldPin: {}", pinChangeRequest.toString(), e);
            throw new DigitalPinStorageException(ERROR_SAVING_PIN_DETAILS);
        }
        return pinChangeResponse;
    }

    @Override
    public ComparePinResponse comparePin(UUID digitalCustomerProfileId, String newPin) {
        log.debug("Comparing new pin for digitalCustomerProfileId: {}", digitalCustomerProfileId);

        try {
            List<DigitalCustomerPin> pinHistoryList = pinRepository.getPinHistory(digitalCustomerProfileId);
            long matchCount = pinHistoryList.stream()
                    .filter(pinHistoryObj -> BCrypt.checkpw(newPin, pinHistoryObj.getOldPin()))
                    .count();

            String pinMatchedResponse = (matchCount > 0) ? PIN_MATCHED : PIN_NOT_MATCHED;
            log.debug("Pin comparison result for digitalCustomerProfileId {}: {}",
                    digitalCustomerProfileId, pinMatchedResponse);

            return new ComparePinResponse(pinMatchedResponse);
        } catch (Exception e) {
            log.debug("Error comparing pin for digitalCustomerProfileId: {}", digitalCustomerProfileId, e);
            throw new PinHistoryRetrievalException("Failed to retrieve pin history", e);
        }
    }
}