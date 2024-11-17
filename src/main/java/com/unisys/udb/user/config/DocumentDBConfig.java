package com.unisys.udb.user.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Collections;

@Configuration
@Slf4j
public class DocumentDBConfig extends AbstractMongoClientConfiguration {

    @Value("${udb.data.mongodb.host}")
    private String host;
    @Value("${udb.data.mongodb.port}")
    private int port;
    @Value("${udb.data.mongodb.database}")
    private String databaseName;
    @Value("${udb.data.mongodb.username}")
    private String username;
    @Value("${udb.data.mongodb.password}")
    private String password;
    @Value("${udb.data.mongodb.retryWrites}")
    private boolean retryWritesEnabled;
    @Value("${udb.data.mongodb.truststore.password}")
    private String trustStorePassword;

    private MongoClient mongoClient;
    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    @Lazy
    public MongoClient mongoClient() {
        if (mongoClient == null) {
            initializeMongoClient();
        }
        return mongoClient;
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }

    private synchronized void initializeMongoClient() {
        if (mongoClient == null) {
            MongoClientSettings.Builder builder = MongoClientSettings.builder();
            builder.applyToClusterSettings(clusterSettingsBuilder ->
                    clusterSettingsBuilder.hosts(Collections.singletonList(new ServerAddress(host, port))));
            builder.credential(MongoCredential.createCredential(username, databaseName, password.toCharArray()));
            log.debug("RetryEnabled:: {} ", retryWritesEnabled);
            builder.retryWrites(retryWritesEnabled);

            try {
                // Load truststore
                InputStream truststoreStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                        "certs/rds-truststore.jks");
                char[] truststorePassword = trustStorePassword.toCharArray();
                KeyStore truststore = KeyStore.getInstance("jks");
                truststore.load(truststoreStream, truststorePassword);

                // Create SSL context with truststore
                log.info("TrustStore default algorithm {}", TrustManagerFactory.getDefaultAlgorithm());
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.
                        getDefaultAlgorithm());
                trustManagerFactory.init(truststore);
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

                log.info("SSL Context initialized {} ", sslContext);
                // Set SSL context in MongoClientSettings
                builder.applyToSslSettings(sslSettingsBuilder -> {
                    sslSettingsBuilder.enabled(true);
                    sslSettingsBuilder.context(sslContext);
                });
                mongoClient = MongoClients.create(builder.build());
            } catch (Exception e) {
                log.error("Failed configuring MongoDB SSL settings", e);
            }
        }
    }
}