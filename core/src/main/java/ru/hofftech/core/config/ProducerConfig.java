package ru.hofftech.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Конфигурация Kafka producers для отправки сообщений.
 * <p>
 * Предоставляет маппинг между типами событий и именами топиков Kafka.
 */
@Configuration
public class ProducerConfig {
    @Value("${spring.cloud.stream.bindings.billingOutbox-out-0.destination}")
    private String billingOutboxTopic;

    /**
     * Создаёт маппинг между типом события и именем топика Kafka.
     *
     * @return карта соответствий "тип события" -> "имя топика"
     */
    @Bean
    public Map<String, String> topics() {
        return Map.of("billingOutbox-out-0", billingOutboxTopic);
    }
}
