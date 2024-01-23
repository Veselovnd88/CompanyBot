package ru.veselov.companybot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Интерфейс управления ботом-ассистентом")
                        .description("Управления отделами, информацией и другими функциями телеграм бота")
                        .version("v2.0.0"));
    }

}
