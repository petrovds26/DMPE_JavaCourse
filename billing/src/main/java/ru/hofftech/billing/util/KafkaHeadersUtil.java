package ru.hofftech.billing.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.MessageHeaders;
import ru.hofftech.shared.model.dto.KafkaHeadersDto;

import java.nio.charset.StandardCharsets;

/**
 * Утилитный класс для работы с заголовками Kafka сообщений.
 * <p>
 * Предоставляет методы для извлечения и преобразования заголовков
 * в DTO и для получения ключа сообщения.
 */
@NullMarked
@UtilityClass
public class KafkaHeadersUtil {

    /**
     * Преобразует заголовки сообщения в DTO.
     *
     * @param headers заголовки сообщения
     * @return DTO с заголовками
     */
    public static KafkaHeadersDto mapToHeaderDto(MessageHeaders headers) {
        return KafkaHeadersDto.builder()
                .messageId(convertToString(headers.get("messageId")))
                .source(convertToString(headers.get("source")))
                .target(convertToString(headers.get("target")))
                .created(convertToString(headers.get("created")))
                .type(convertToString(headers.get("type")))
                .payloadVersion(convertToString(headers.get("payloadVersion")))
                .eventType(convertToString(headers.get("eventType")))
                .build();
    }

    /**
     * Преобразует объект в строку.
     * <p>
     * Поддерживаемые типы:
     * <ul>
     *   <li>null → "null"</li>
     *   <li>String → возвращается как есть</li>
     *   <li>byte[] → декодируется в UTF-8</li>
     *   <li>остальные типы → "null"</li>
     * </ul>
     *
     * @param obj объект для преобразования
     * @return строковое представление объекта
     */
    public static String convertToString(@Nullable Object obj) {
        return switch (obj) {
            case null -> "null";
            case String string -> string;
            case byte[] bytes -> new String(bytes, StandardCharsets.UTF_8);
            case Object other -> "null";
        };
    }

    /**
     * Извлекает ключ сообщения из заголовков Kafka.
     *
     * @param headers заголовки сообщения
     * @return ключ сообщения
     */
    public static String mapToKey(MessageHeaders headers) {
        return convertToString(headers.get("kafka_receivedMessageKey"));
    }
}
