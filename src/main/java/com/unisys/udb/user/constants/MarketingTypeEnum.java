package com.unisys.udb.user.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MarketingTypeEnum {
    EMAIL("hdrEmail"),
    SMS("hdrSMS"),
    POST("hdrPost"),
    TELEPHONE("hdrTelephone"),
    ONLINE("hdrOnline");

    private final String elementName;

    public static MarketingTypeEnum fromElementName(String elementName) {
        for (MarketingTypeEnum type : values()) {
            if (type.getElementName().equalsIgnoreCase(elementName)) {
                return type;
            }
        }
        return null;
    }
}
