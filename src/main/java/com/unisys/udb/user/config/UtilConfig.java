package com.unisys.udb.user.config;

import com.unisys.udb.utility.config.DateUtilConfig;
import com.unisys.udb.utility.config.LocaleConfig;
import com.unisys.udb.utility.util.DateUtil;
import com.unisys.udb.utility.util.NumberFormatter;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "util.config")
public class UtilConfig {
    private DateUtilConfig dateFormatter;
    private LocaleConfig localeConfig;

    /**
     * Initializes utility classes after bean construction.
     */
    @PostConstruct
    public void initUtils() {
        if (localeConfig != null) {
            NumberFormatter.init(localeConfig);
        }
        if (dateFormatter != null) {
            DateUtil.init(dateFormatter);
        }
    }
}
