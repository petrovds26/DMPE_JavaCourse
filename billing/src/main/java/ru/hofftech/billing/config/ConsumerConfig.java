package ru.hofftech.billing.config;

import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import ru.hofftech.billing.service.BillingService;
import ru.hofftech.shared.model.dto.BillingDto;

import java.util.function.Consumer;

/**
 * Конфигурация Kafka consumers для обработки входящих сообщений.
 * <p>
 * Определяет бины для потребления сообщений из топиков Kafka
 * и их маршрутизацию к соответствующим слушателям.
 */
@NullMarked
@Configuration
public class ConsumerConfig {

    /**
     * Создаёт консьюмер для обработки сообщений биллинга.
     *
     * @param billingService сервис биллинга
     * @return функция-консьюмер для обработки сообщений
     */
    @Bean
    public Consumer<Message<BillingDto>> billingInbox(BillingService billingService) {
        return message -> billingService.listener(message.getPayload(), message.getHeaders());
    }
}
