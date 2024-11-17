package com.unisys.udb.user.utils;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

public final class Kafka {
    // Private constructor to hide the implicit public one
    private Kafka() {
        throw new IllegalStateException("Utility class");
    }

    public static Map<String, Object> initializeKafkaProps(String bootstrapServers) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return configProps;
    }
}
