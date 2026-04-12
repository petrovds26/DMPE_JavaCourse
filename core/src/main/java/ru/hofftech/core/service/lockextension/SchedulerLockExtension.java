package ru.hofftech.core.service.lockextension;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Расширение для продления блокировок ShedLock во время выполнения длительных задач.
 * <p>
 * Позволяет периодически обновлять время блокировки в базе данных,
 * чтобы задача не была прервана другими инстансами приложения.
 */
@Slf4j
public class SchedulerLockExtension {
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final AtomicReference<ScheduledFuture<?>> lockExtensionFuture = new AtomicReference<>();
    private final JdbcTemplate jdbcTemplate;
    private final String lockName;
    private final int updateLockIntervalInSeconds;

    /**
     * Конструктор расширения блокировки.
     *
     * @param jdbcTemplate               JDBC шаблон для работы с БД
     * @param lockName                    имя блокировки
     * @param updateLockIntervalInSeconds интервал обновления блокировки в секундах
     */
    public SchedulerLockExtension(JdbcTemplate jdbcTemplate, String lockName, int updateLockIntervalInSeconds) {
        this.jdbcTemplate = jdbcTemplate;
        this.lockName = lockName;
        this.updateLockIntervalInSeconds = updateLockIntervalInSeconds;
    }

    /**
     * Запускает таймер для периодического продления блокировки.
     * <p>
     * Отменяет предыдущий таймер, если он был запущен.
     */
    public void startLockExtensionTimer() {
        log.info("Concurrent Job {}  executing at {}", lockName, LocalDateTime.now(ZoneOffset.UTC));

        ScheduledFuture<?> newFuture = executorService.scheduleAtFixedRate(
                this::extendLock, updateLockIntervalInSeconds, updateLockIntervalInSeconds, TimeUnit.SECONDS);

        ScheduledFuture<?> previousFuture = lockExtensionFuture.getAndSet(newFuture);

        if (previousFuture != null) {
            previousFuture.cancel(false);
        }
    }

    /**
     * Останавливает таймер продления блокировки.
     */
    public void stopLockExtensionTimer() {
        log.info("Concurrent Job {}  executed at {}", lockName, LocalDateTime.now(ZoneOffset.UTC));

        ScheduledFuture<?> future = lockExtensionFuture.getAndSet(null);
        if (future != null) {
            future.cancel(false);
        }
    }

    /**
     * Продлевает время блокировки в базе данных.
     * <p>
     * Обновляет поле lock_until на текущее время + интервал,
     * если блокировка всё ещё принадлежит текущему инстансу.
     */
    private void extendLock() {
        try {
            jdbcTemplate.update(
                    "UPDATE shedlock SET lock_until = ? WHERE name = ? AND lock_until < NOW()",
                    LocalDateTime.now(ZoneOffset.UTC).plusSeconds(updateLockIntervalInSeconds),
                    lockName);
        } catch (Exception e) {
            log.error("Ошибка обновления блокировки {}: {} ", lockName, LocalDateTime.now(ZoneOffset.UTC));
        }
    }
}
