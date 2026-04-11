package ru.hofftech.core.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

import java.util.TimeZone;

/**
 * Конфигурация планировщика задач с поддержкой распределённых блокировок ShedLock.
 * <p>
 * Обеспечивает эксклюзивное выполнение запланированных задач
 * в кластеризованной среде с использованием базы данных.
 */
@Configuration
@ConditionalOnProperty(name = "scheduling.enabled", havingValue = "true", matchIfMissing = true)
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class SchedulerConfig {

    /**
     * Создаёт провайдер блокировок на основе JDBC.
     * <p>
     * Использует таблицу shedlock в базе данных для хранения информации
     * о блокировках между инстансами приложения.
     *
     * @param dataSource источник данных для подключения к БД
     * @return провайдер блокировок для ShedLock
     */
    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .withTableName("shedlock")
                .withTimeZone(TimeZone.getTimeZone("UTC"))
                .build());
    }
}
