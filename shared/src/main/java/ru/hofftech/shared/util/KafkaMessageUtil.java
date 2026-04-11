package ru.hofftech.shared.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.dto.KafkaHeadersDto;

import java.util.HashMap;
import java.util.Map;

/**
 * Утилитный класс для работы с Kafka сообщениями.
 * <p>
 * Предоставляет методы для преобразования между DTO заголовков
 * и картой заголовков, используемой в Kafka.
 */
@NullMarked
@UtilityClass
public class KafkaMessageUtil {

    /**
     * Преобразует DTO заголовков в карту для Kafka.
     * <p>
     * Если передан null, возвращает пустую карту.
     *
     * @param headers DTO с заголовками (может быть null)
     * @return карта заголовков, где ключи соответствуют именам полей DTO
     */
    public static Map<String, Object> headerDtoToMap(@Nullable KafkaHeadersDto headers) {
        Map<String, Object> headersMap = new HashMap<>();
        if (headers != null) {
            headersMap.put("messageId", headers.messageId());
            headersMap.put("source", headers.source());
            headersMap.put("target", headers.target());
            headersMap.put("created", headers.created());
            headersMap.put("type", headers.type());
            headersMap.put("payloadVersion", headers.payloadVersion());
            headersMap.put("eventType", headers.eventType());
        }
        return headersMap;
    }
}
