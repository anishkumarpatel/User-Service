package com.unisys.udb.user.utils.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.BiometricStatusDTO;
import com.unisys.udb.user.dto.request.DigitalCustomerShortcutsRequest;
import com.unisys.udb.user.dto.request.TermsConditionsAndCookiesRequest;
import com.unisys.udb.user.dto.response.BiometricStatusResponse;
import com.unisys.udb.user.exception.DigitalCustomerProfileIdNotFoundException;


import com.unisys.udb.user.exception.InvalidRequestException;
import com.unisys.udb.user.exception.MissingRequiredRequestParamException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static com.unisys.udb.user.constants.UdbConstants.*;
import static java.util.Objects.nonNull;
@Slf4j
public final class CommonUtil {

    private static final Map<UUID, BiometricStatusResponse> BIOMETRIC_STATUS_MAP = new HashMap<>();

    static {

        BIOMETRIC_STATUS_MAP.put(UUID.fromString("8869a9ba-5240-44bf-9491-0e6798581f49"),
                new BiometricStatusResponse(true, true));
        BIOMETRIC_STATUS_MAP.put(UUID.fromString("9302B64C-79B3-4E70-BFBE-67F252A77E86"),
                new BiometricStatusResponse(true, false));
        BIOMETRIC_STATUS_MAP.put(UUID.fromString("D7FE4B14-1F1F-4583-A837-A6160A672781"),
                new BiometricStatusResponse(false, true));

    }

    public static String updateBiometricStatus(BiometricStatusDTO request, UUID customerProfileId) {
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        List<String> params = new ArrayList<>();
        params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));

        BiometricStatusResponse biometricStatus = BIOMETRIC_STATUS_MAP.get(customerProfileId);

        if (request.getFaceId() == null && request.getTouchId() == null) {
            throw new InvalidRequestException("Either faceId or touchId cannot be null");
        }

        if (BIOMETRIC_STATUS_MAP.containsKey(customerProfileId)) {
            if (request.getFaceId() != null) {
                biometricStatus.setFaceId(request.getFaceId());
            }

            if (request.getTouchId() != null) {
                biometricStatus.setTouchId(request.getTouchId());
            }

            BIOMETRIC_STATUS_MAP.put(customerProfileId, biometricStatus);

        } else {
            throw new DigitalCustomerProfileIdNotFoundException(
                    errorCode,
                    HttpStatus.NOT_FOUND,
                    "FAILURE",
                    NOT_FOUND_ERROR_MESSAGE + customerProfileId,
                    params
            );
        }
        return "Biometric status updated successfully";
    }

    private CommonUtil() {
    }

    private static ObjectNode getJsonNodes(DigitalCustomerShortcutsRequest shortcutsRequest)
            throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(shortcutsRequest);
        return (ObjectNode) new ObjectMapper().readTree(json);
    }

    public static boolean validateMandatoryFields(DigitalCustomerShortcutsRequest shortcutsRequest)
            throws JsonProcessingException {
        ObjectNode dataNode = getJsonNodes(shortcutsRequest);
        if (!nonNull(dataNode) || dataNode.isEmpty()
                || (!StringUtils.isEmpty(dataNode.toString())
                && dataNode.toString().trim().equals(UdbConstants.EMPTY_REQUEST_BODY))) {
            return false;
        } else {
            int count = 0;
            // Get no of fields present in the request.
            count = getNoOfShortCutsPresent(count, dataNode);
            return count >= UdbConstants.THREE_CONSTANT;
        }
    }

    private static int getNoOfShortCutsPresent(int count, final ObjectNode dataNode) {
        count = getFieldCount(dataNode, count, UdbConstants.ShortcutEnum.FUND_TRANSFER_SHORTCUT.getShortcutValue());
        count = getFieldCount(dataNode, count, UdbConstants.ShortcutEnum.E_STATEMENT_SHORTCUT.getShortcutValue());
        count = getFieldCount(dataNode, count, UdbConstants.ShortcutEnum.PAYEE_SHORTCUT.getShortcutValue());
        count = getFieldCount(dataNode, count, UdbConstants.ShortcutEnum.SCHEDULED_PAYMENTS_SHORTCUT
                .getShortcutValue());
        count = getFieldCount(dataNode, count, UdbConstants.ShortcutEnum.COMM_PREF_SHORTCUT.getShortcutValue());
        count = getFieldCount(dataNode, count, UdbConstants.ShortcutEnum.SESSION_HISTORY_SHORTCUT.getShortcutValue());
        return count;
    }
    public static String getCurrentFormattedTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentTime.format(formatter);
    }

    private static int getFieldCount(final ObjectNode dataNode, int count, final String fieldName) {
        if (dataNode.has(fieldName)
                && !StringUtils.isEmpty(dataNode.get(fieldName).asText())
                && dataNode.get(fieldName).asText().equals(UdbConstants.BOOLEAN_TRUE)) {
            count++;
        }
        return count;
    }
    public static Date getCurrentDateInUTC() {
        return Date.from(ZonedDateTime.now(ZoneId.of("UTC")).toInstant());
    }

    public static void validateMandatoryFieldsTermsAndConditions(
            TermsConditionsAndCookiesRequest termsConditionsAndCookiesRequest, String termsConditionParam) {
        ObjectNode dataNode = null;
        try {
            dataNode = getTermsAndConditionNodes(termsConditionsAndCookiesRequest);
        } catch (JsonProcessingException e) {
            throw new InvalidRequestException("validateMandatoryFieldsTermsAndConditions");
        }
        List<String> missingFields = new ArrayList<>();
        if (!nonNull(dataNode) || dataNode.isEmpty()
                || (!StringUtils.isEmpty(dataNode.toString())
                && dataNode.toString().trim().equals(UdbConstants.EMPTY_REQUEST_BODY))) {
            throw new MissingRequiredRequestParamException("updateField");
        } else if (termsConditionParam.equals(UdbConstants.UPDATE_TERMS_CONDITIONS)) {
            if (!doesDataNodeContainsField(dataNode, UdbConstants.TermsEnum.TERMS_AND_CONDITIONS)) {
                missingFields.add(UdbConstants.TermsEnum.TERMS_AND_CONDITIONS.getEnumValue());
            }
        } else if (termsConditionParam.equals(UdbConstants.UPDATE_COOKIES)) {
            if (!doesDataNodeContainsField(dataNode, UdbConstants.CookiesEnum.FUNCTIONAL_COOKIE)) {
                missingFields.add(UdbConstants.CookiesEnum.FUNCTIONAL_COOKIE.getEnumValue());
            }
            if (!doesDataNodeContainsField(dataNode, UdbConstants.CookiesEnum.STRICTLY_ACCEPTABLE_COOKIE)) {
               missingFields.add(UdbConstants.CookiesEnum.STRICTLY_ACCEPTABLE_COOKIE.getEnumValue());
            }
            if (!doesDataNodeContainsField(dataNode, UdbConstants.CookiesEnum.PERFORMANCE_COOKIE)) {
                missingFields.add(UdbConstants.CookiesEnum.PERFORMANCE_COOKIE.getEnumValue());
            }
        }
        if (!missingFields.isEmpty()) {
            throw new MissingRequiredRequestParamException(missingFields.toString());
        }
    }

    private static  <T extends UdbConstants.CommonEnumFields> boolean doesDataNodeContainsField(
            ObjectNode dataNode, T field) {
        return  dataNode.has(field.getEnumValue()) && !StringUtils.isEmpty(dataNode.get(field.getEnumValue()).asText());
    }

    private static ObjectNode getTermsAndConditionNodes(TermsConditionsAndCookiesRequest shortcutsRequest)
            throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(shortcutsRequest);
        return (ObjectNode) new ObjectMapper().readTree(json);
    }


}