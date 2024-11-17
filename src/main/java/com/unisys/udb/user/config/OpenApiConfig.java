package com.unisys.udb.user.config;

import com.unisys.udb.user.constants.UdbConstants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
public class OpenApiConfig {
    @Bean
    public OpenAPI userMicroserviceOpenAPI() {
        return new OpenAPI()
         .info(new Info().title(UdbConstants.USER_SERVICE)
                .description(UdbConstants.USER_API_DESCRIPTION).version("1.0"));
    }
}