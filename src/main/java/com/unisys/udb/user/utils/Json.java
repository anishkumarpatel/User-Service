package com.unisys.udb.user.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Json {

    // Private constructor to hide the implicit public one
    private Json() {
        throw new IllegalStateException("Utility class");
    }

    public static String serialize(Object object) {
        // Create an instance of ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            // Convert the object to a JSON string
            return objectMapper.writeValueAsString(object);

        } catch (Exception exception) {
            log.error("Error occurred while converting object to json string", exception);
        }
        return null;
    }

    public static <T> T deserialize(String jsonString, Class<T> type) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            // Deserialize JSON string into an object of a specific class based on type
            return objectMapper.readValue(jsonString, type);
        } catch (Exception exception) {
            log.error("Error occurred while converting json string to object", exception);
        }
        return null;
    }
}
