package ru.hofftech.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Конфигурация Swagger/OpenAPI для документации REST API.
 */
@NullMarked
@Configuration
@Profile("!prod")
public class SwaggerConfig {

    /**
     * Создаёт и настраивает OpenAPI документацию.
     *
     * @return сконфигурированный объект OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("Core").description("API основного сервиса"));
    }
}
