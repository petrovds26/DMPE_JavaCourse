package ru.hofftech.core.service.lockextension;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Фабрика для создания экземпляров {@link SchedulerLockExtension}.
 * <p>
 * Позволяет создавать расширения блокировок с необходимыми зависимостями.
 */
@Component
@RequiredArgsConstructor
public class SchedulerLockExtensionFactory {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Создаёт новое расширение блокировки.
     *
     * @param lockName                    имя блокировки
     * @param updateLockIntervalInMinutes интервал обновления блокировки в минутах
     * @return новый экземпляр {@link SchedulerLockExtension}
     */
    public SchedulerLockExtension create(String lockName, int updateLockIntervalInMinutes) {
        return new SchedulerLockExtension(jdbcTemplate, lockName, updateLockIntervalInMinutes);
    }
}
