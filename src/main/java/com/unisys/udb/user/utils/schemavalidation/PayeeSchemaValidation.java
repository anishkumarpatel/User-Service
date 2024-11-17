package com.unisys.udb.user.utils.schemavalidation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.unisys.udb.user.dto.request.PayeeRequest;
import com.unisys.udb.user.exception.InvalidArgumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PayeeSchemaValidation {
    private final  ObjectMapper objectMapper;
    public void validateSchema(PayeeRequest payeeRequest) {

        JsonNode inpData = objectMapper.valueToTree(payeeRequest);
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                "schema/payeeSchema.json");
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        JsonSchema schema = schemaFactory.getSchema(input);
        Set<ValidationMessage> result = schema.validate(inpData);
        if (!result.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Schema error");
            result.forEach(error -> errorMessage.append(error.getMessage()).append("; "));
            throw new InvalidArgumentException(errorMessage.toString());
        }
    }
}
