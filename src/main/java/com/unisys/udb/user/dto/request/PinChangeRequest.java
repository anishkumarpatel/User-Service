package com.unisys.udb.user.dto.request;

import com.unisys.udb.user.annotation.LogMask;
import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.utils.validators.annotation.ValidatePin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PinChangeRequest {



    @NotNull
    private UUID digitalCustomerProfileId;

    @NotNull
    @LogMask(prefix = UdbConstants.PREFIX)
    private String oldPin;

    @NotBlank(message = "PIN should be not null")
    @ValidatePin(message = "Invalid PIN", key = "validation.pin")
    @LogMask(prefix = UdbConstants.PREFIX)
    private String newPin;

    @NotNull
    private String userName;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PinChangeRequest {");
        builder.append("digitalCustomerProfileId='").append(digitalCustomerProfileId).append('\'');


        if (oldPin != null) {
            int visibleLength = UdbConstants.PREFIX;
            int maskedLength = oldPin.length() - visibleLength;
            String maskedValue = StringUtils.repeat("*", maskedLength);

            String visiblePrefix = oldPin.substring(0, visibleLength);
            builder.append(", oldPin='").append(visiblePrefix).append(maskedValue).append('\'');
        } else {
            builder.append(", oldPin=null");
        }

        if (newPin != null) {
            int visibleLength = UdbConstants.PREFIX;
            int maskedLength = newPin.length() - visibleLength;
            String maskedValue = StringUtils.repeat("*", maskedLength);

            String visiblePrefix = newPin.substring(0, visibleLength);
            builder.append(", newPin='").append(visiblePrefix).append(maskedValue).append('\'');
        } else {
            builder.append(", newPin=null");
        }

        builder.append(", userName='").append(userName).append('\'');
        builder.append('}');
        return builder.toString();
    }
}