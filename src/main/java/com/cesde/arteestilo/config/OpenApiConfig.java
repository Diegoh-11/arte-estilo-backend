package com.cesde.arteestilo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI arteEstiloOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Arte y Estilo API")
                .version("1.0.0")
                .description("API REST de la tienda Arte y Estilo: productos, categorías, usuarios y pedidos."));
    }
}
