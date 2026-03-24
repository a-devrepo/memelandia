package com.nca.memeservice.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NCA Meme Service API")
                        .version("1.0")
                        .description("Serviço de gerenciamento de Memes com rastreamento distribuído."))
                .servers(List.of(
                        new Server().url("/meme-service").description("Acesso via API Gateway")
                ));
    }
}
