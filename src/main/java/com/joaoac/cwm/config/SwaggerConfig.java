package com.joaoac.cwm.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Crypto Wallet API")
                        .version("1.0.0")
                        .description("API para gerenciamento de carteiras de criptomoedas")
                        .contact(new Contact()
                                .name("João AC")
                                .email("joao.ac1406@gmail.com")
                                .url("https://github.com/joao-ac")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://crypto-wallet-manager-api-production.up.railway.app")
                                .description("Servidor de Produção (Railway)")
                ));
    }
}
