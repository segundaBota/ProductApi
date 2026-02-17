package org.example.jle.productapi.config;

import jakarta.annotation.PostConstruct;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @PostConstruct
    public void init() {
        SpringDocUtils.getConfig().replaceWithClass(JsonNullable.class, Object.class);
    }

}
