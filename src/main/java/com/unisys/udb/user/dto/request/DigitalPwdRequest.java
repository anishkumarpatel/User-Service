package com.unisys.udb.user.dto.request;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unisys.udb.user.annotation.LogMask;
import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.utils.masking.DigitalPwdRequestSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonSerialize(using = DigitalPwdRequestSerializer.class)
public class DigitalPwdRequest {


    private UUID digitalProfileId;
    @LogMask(prefix = UdbConstants.PREFIX)
    private String password;


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DigitalPwdRequest {");
        builder.append("digitalCustomerProfileId='").append(digitalProfileId).append('\'');


        if (password != null) {
            int visibleLength = UdbConstants.PREFIX;
            int maskedLength = password.length() - visibleLength;
            String maskedValue = StringUtils.repeat("*", maskedLength);

            String visiblePrefix = password.substring(0, visibleLength);
            builder.append(", password='").append(visiblePrefix).append(maskedValue).append('\'');
        } else {
            builder.append(", password=null");
        }
        builder.append('}');
        return builder.toString();
    }
}
