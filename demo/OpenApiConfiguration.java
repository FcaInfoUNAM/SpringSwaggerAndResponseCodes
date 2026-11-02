package com.example.demo;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfiguration {
    @Bean
    OpenAPI productApi() {
        return new OpenAPI().info(new Info()
                .title("API Spring Tienda de ejemplo")
                .description("API de productos, categorías, comentarios e imágenes")
                .version("1.0.0"));
    }
}