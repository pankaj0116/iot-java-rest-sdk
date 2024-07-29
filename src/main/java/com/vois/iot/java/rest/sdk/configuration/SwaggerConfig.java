package com.vois.iot.java.rest.sdk.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SwaggerConfiguration class
 * This is Custom Configuration for Swagger UI (Optional)
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI createCustomOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("IOT Devices Tracker API")
                        .version("1.0")
                        .description("IOT Devices Tracker API Documentation"));
    }
}
