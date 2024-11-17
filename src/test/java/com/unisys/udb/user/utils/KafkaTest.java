package com.unisys.udb.user.utils;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KafkaTest {

    @Test
    void initializeKafkaPropsProducerSuccess() {
        // Define bootstrap servers
        String bootstrapServers = "localhost:9092";

        // Initialize Kafka properties for producer
        Map<String, Object> props = Kafka.initializeKafkaProps(bootstrapServers);

        // Verify producer configuration
        assertEquals(bootstrapServers, props.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(StringSerializer.class.getName(), props.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(StringSerializer.class.getName(), props.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }

    @Test
    void initializeKafkaPropsConsumerSuccess() {
        // Define bootstrap servers
        String bootstrapServers = "localhost:9092";

        // Initialize Kafka properties for consumer
        Map<String, Object> props = Kafka.initializeKafkaProps(bootstrapServers);

        // Verify consumer configuration
        assertEquals(bootstrapServers, props.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        // Add more assertions for consumer properties if needed
    }
}
