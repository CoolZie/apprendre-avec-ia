package com.exercice1.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Configuration OpenAPI/Swagger pour la documentation de l'API
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Product Management API")
                .description("API REST complète pour la gestion d'un catalogue de produits avec recherche avancée, filtrage multicritères, pagination et statistiques")
                .version("2.0")
                .contact(new Contact()
                    .name("Équipe Backend Formation")
                    .email("formation@backend.dev")
                    .url("https://github.com/formation-backend"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Serveur de développement local"),
                new Server()
                    .url("https://api.formation-backend.dev")
                    .description("Serveur de production")
            ));
    }
}
