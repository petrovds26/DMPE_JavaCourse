package ru.hofftech.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Свойства конфигурации для планировщика отправки событий в сервис биллинга.
 * <p>
 * Содержит настройки для задания периодичности запуска, интервалов блокировки
 * и идентификации задачи в распределённой среде.
 */
@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "scheduler.billing-outbox")
public class BillingOutboxProperties {

    /**
     * Название задания в планировщике.
     * Используется для идентификации в ShedLock.
     */
    private String nameScheduler;

    /**
     * Интервал блокировки задания в минутах.
     * Определяет, как долго задание может выполняться эксклюзивно.
     */
    private int lockIntervalMinutes;

    /**
     * CRON выражение для периодического запуска задания.
     */
    private String interval;
}
