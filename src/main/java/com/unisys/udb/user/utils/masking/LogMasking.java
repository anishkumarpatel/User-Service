package com.unisys.udb.user.utils.masking;
import java.util.UUID;


public final class LogMasking {

    private static final int VISIBLE_LENGTH = 4;

    private LogMasking() {
    }
    public static String maskingDigitlProfileId(UUID digitalProfileID) {
        if (digitalProfileID == null) {
            return null;
        }

        String uuidStr = digitalProfileID.toString();
        int maskedLength = uuidStr.length() - VISIBLE_LENGTH;
        String maskedValue = "*".repeat(maskedLength);

        String visiblePrefix = uuidStr.substring(0, VISIBLE_LENGTH);
        return visiblePrefix + maskedValue;
    }

}





