package com.unisys.udb.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {


    @Value("${app.config.value}")
    private String readKeyFromYaml;

    @Value("${login.config.count}")
    private int loginCount;

    public String getKeyFromYaml() {
        return readKeyFromYaml;
    }

    public Integer getLoginCount() {
        return loginCount;
    }
}
