package com.unisys.udb.user.config;

import com.unisys.udb.utility.auditing.handler.KafkaErrorHandler;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.HashMap;
import java.util.Map;

import static com.unisys.udb.user.constants.UdbConstants.*;


@EnableAsync
@Configuration
public class KafkaConfiguration {
    @Value("${aws.kafka.bootstrap.server}")
    private String bootstrapServers;

    @Value("${aws.kafka.access-key-id}")
    private String systemKafkaAccessKeyId;

    @Value("${aws.kafka.secret-access-key}")
    private String systemKafkaSecretKey;


    @Bean
    @Primary
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
        props.put(ProducerConfig.RETRIES_CONFIG, THREE_CONSTANT);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, THIRTY_THOUSAND_CONSTANT);
        props.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, SIXTY_THOUSAND_CONSTANT);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, SIXTY_THOUSAND_CONSTANT);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, SIXTY_THOUSAND_CONSTANT);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, ONE_CONSTANT);
        props.put("sasl.mechanism", "AWS_MSK_IAM");
        props.put("sasl.jaas.config", "software.amazon.msk.auth.iam.IAMLoginModule required "
                + "username=\"" + systemKafkaAccessKeyId + "\" password=\"" + systemKafkaSecretKey + "\";");
        return new DefaultKafkaProducerFactory<>(props);
    }


    @Bean
    @Primary
    public KafkaTemplate<String, String> kafkaTemplate() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setProducerListener(new KafkaErrorHandler());

        return kafkaTemplate;

    }

}