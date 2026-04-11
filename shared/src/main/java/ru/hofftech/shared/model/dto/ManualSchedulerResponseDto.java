package ru.hofftech.shared.model.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO ответа при ручном управлении планировщиком.
 * <p>
 * Используется для возврата информации о состоянии выполнения
 * запланированной задачи (например, отправки данных в биллинг).
 *
 * @param lastStartExecuteDt дата и время последнего запуска задачи
 * @param lastEndExecuteDt   дата и время последнего завершения задачи
 * @param message            информационное сообщение о статусе
 */
@Builder
public record ManualSchedulerResponseDto(
        LocalDateTime lastStartExecuteDt, LocalDateTime lastEndExecuteDt, String message) {}
