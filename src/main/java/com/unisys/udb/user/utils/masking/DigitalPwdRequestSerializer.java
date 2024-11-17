package com.unisys.udb.user.utils.masking;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.DigitalPwdRequest;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class DigitalPwdRequestSerializer extends JsonSerializer<DigitalPwdRequest> {

    @Override
    public void serialize(DigitalPwdRequest value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("digitalProfileId", String.valueOf(value.getDigitalProfileId()));

        if (value.getPassword() != null) {
            int visibleLength = UdbConstants.PREFIX;
            int maskedLength = value.getPassword().length() - visibleLength;
            String maskedValue = StringUtils.repeat("*", maskedLength);
            String visiblePrefix = value.getPassword().substring(0, visibleLength);
            gen.writeStringField("password", visiblePrefix + maskedValue);
        } else {
            gen.writeStringField("password", null);
        }

        gen.writeEndObject();
    }
}