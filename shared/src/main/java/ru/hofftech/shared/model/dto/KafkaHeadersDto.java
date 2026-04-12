package ru.hofftech.shared.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Заголовки сообщения для публикации в Kafka.
 * <p>
 * Содержит метаинформацию о сообщении, необходимую для маршрутизации
 * и обработки в целевых системах.
 *
 * @param messageId      уникальный идентификатор сообщения (для дедупликации)
 * @param source         источник сообщения (например, "core", "billing")
 * @param target         целевая система (например, "billing", "analytics")
 * @param created        дата и время создания сообщения
 * @param type           тип сообщения (например, "JSON", "AVRO")
 * @param payloadVersion версия формата полезной нагрузки
 * @param eventType      тип события (например, "BILLING_CREATE", "BILLING_UPDATE")
 */
@Builder
public record KafkaHeadersDto(
        @NotNull String messageId,
        @NotNull String source,
        @NotNull String target,
        @NotNull String created,
        @NotNull String type,
        @NotNull String payloadVersion,
        @NotNull String eventType) {}
