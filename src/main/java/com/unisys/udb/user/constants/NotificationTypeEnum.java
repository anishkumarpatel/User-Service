package com.unisys.udb.user.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationTypeEnum {
    PUSH("hdrMobilePush"),
    SMS("hdrSMS"),
    EMAIL("hdrEmail");

    private final String elementName;

    public static NotificationTypeEnum fromElementName(String elementName) {
        for (NotificationTypeEnum type : values()) {
            if (type.getElementName().equalsIgnoreCase(elementName)) {
                return type;
            }
        }
        return null;
    }
}
