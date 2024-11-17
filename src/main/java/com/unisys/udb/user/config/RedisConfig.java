package com.unisys.udb.user.config;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.utility.auditing.handler.RedisConnectionFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {
    @Value("${udb.redis.hostname}")
    private String hostName;
    @Value("${udb.redis.port}")
    private int port;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        try {
            RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
            configuration.setHostName(hostName);
            configuration.setPort(port);
            return new JedisConnectionFactory(configuration);
        } catch (Exception exception) {
            throw new RedisConnectionFactoryException(UdbConstants.REDIS_CONNECTION_FAILURE, exception);
        }
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        try {
            template.setConnectionFactory(this.jedisConnectionFactory());
            template.setKeySerializer(new StringRedisSerializer());
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new JdkSerializationRedisSerializer());
            template.setEnableTransactionSupport(true);
            template.afterPropertiesSet();
            return template;
        } catch (RuntimeException runtimeException) {
            throw new RedisConnectionFactoryException(UdbConstants.REDIS_TEMPLATE_FAILURE, runtimeException);
        }
    }
}

