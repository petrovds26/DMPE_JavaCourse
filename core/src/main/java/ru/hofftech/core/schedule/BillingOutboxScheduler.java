package ru.hofftech.core.schedule;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.hofftech.core.config.BillingOutboxProperties;
import ru.hofftech.core.model.entity.ShedlockEntity;
import ru.hofftech.core.repository.ShedlockRepository;
import ru.hofftech.core.service.BillingOutboxService;
import ru.hofftech.core.service.lockextension.SchedulerLockExtension;
import ru.hofftech.core.service.lockextension.SchedulerLockExtensionFactory;
import ru.hofftech.shared.model.dto.ManualSchedulerResponseDto;

import java.time.LocalDateTime;

/**
 * Планировщик для асинхронного присвоение трек-номеров из Яндекса.
 * <p>
 * Использует ShedLock для распределённой блокировки в кластере
 * и SchedulerLockExtension для продления блокировки во время выполнения.
 */
@Component
public class BillingOutboxScheduler {

    private final BillingOutboxService billingOutboxService;
    private final SchedulerLockExtension schedulerLockExtension;
    private final ShedlockRepository shedlockRepository;
    private final BillingOutboxProperties jobProperties;
    private final BillingOutboxScheduler self;

    /**
     * Конструктор планировщика.
     *
     * @param billingOutboxService           сервис отложенной отправки в Биллинг
     * @param schedulerLockExtensionFactory  фабрика расширений блокировки
     * @param jobProperties                  свойства задачи
     */
    public BillingOutboxScheduler(
            BillingOutboxService billingOutboxService,
            ShedlockRepository shedlockRepository,
            SchedulerLockExtensionFactory schedulerLockExtensionFactory,
            BillingOutboxProperties jobProperties) {
        this.billingOutboxService = billingOutboxService;
        this.shedlockRepository = shedlockRepository;
        this.jobProperties = jobProperties;
        this.self = this;
        this.schedulerLockExtension = schedulerLockExtensionFactory.create(
                jobProperties.getNameScheduler(), jobProperties.getLockIntervalMinutes());
    }

    /**
     * Запускает асинхронное присвоение трек-номеров по расписанию.
     * <p>
     * Выполняется асинхронное присвоение трек-номеров с распределённой блокировкой.
     */
    @Scheduled(cron = "#{billingOutboxProperties.interval}")
    @SchedulerLock(
            name = "#{billingOutboxProperties.nameScheduler}",
            lockAtMostFor = "#{'PT' + billingOutboxProperties.lockIntervalMinutes + 'M'}")
    public void send() {
        try {
            schedulerLockExtension.startLockExtensionTimer();
            billingOutboxService.handle();
        } finally {
            schedulerLockExtension.stopLockExtensionTimer();
        }
    }
    /**
     * Запускает асинхронное присвоение трек-номеров асинхронно.
     */
    @Async
    @SchedulerLock(
            name = "#{billingOutboxProperties.nameScheduler}",
            lockAtMostFor = "#{'PT' + billingOutboxProperties.lockIntervalMinutes + 'M'}")
    public void sendAsync() {
        send();
    }

    /**
     * Возвращает статус задачи
     *
     * @return DTO с информацией о статусе задачи
     */
    public ManualSchedulerResponseDto receiveStatus() {
        ShedlockEntity shedlockEntity = shedlockRepository.findByName(jobProperties.getNameScheduler());

        if (shedlockEntity == null) {
            return ManualSchedulerResponseDto.builder()
                    .lastStartExecuteDt(null)
                    .lastEndExecuteDt(null)
                    .message("Задача не была запущена")
                    .build();
        } else if (shedlockEntity.getLockUntil().isBefore(LocalDateTime.now())) {
            return ManualSchedulerResponseDto.builder()
                    .lastStartExecuteDt(shedlockEntity.getLockedAt())
                    .lastEndExecuteDt(shedlockEntity.getLockUntil())
                    .message("Задача не заблокирована. Последний запуск " + shedlockEntity.getLockedAt())
                    .build();
        }

        return ManualSchedulerResponseDto.builder()
                .lastStartExecuteDt(shedlockEntity.getLockedAt())
                .lastEndExecuteDt(null)
                .message("Задача заблокирована. Последний запуск " + shedlockEntity.getLockedAt())
                .build();
    }

    /**
     * Запускает задачу вручную.
     *
     * @return DTO с информацией о результате запуска
     */
    public ManualSchedulerResponseDto executeScheduler() {
        ShedlockEntity shedlockEntity = shedlockRepository.findByName(jobProperties.getNameScheduler());

        if (shedlockEntity == null || shedlockEntity.getLockUntil().isBefore(LocalDateTime.now())) {
            self.sendAsync();

            return ManualSchedulerResponseDto.builder()
                    .lastStartExecuteDt(LocalDateTime.now())
                    .lastEndExecuteDt(null)
                    .message("Задача выполняется")
                    .build();
        }

        return ManualSchedulerResponseDto.builder()
                .lastStartExecuteDt(shedlockEntity.getLockedAt())
                .lastEndExecuteDt(null)
                .message("Задача заблокирована. Последний запуск " + shedlockEntity.getLockedAt())
                .build();
    }
}
