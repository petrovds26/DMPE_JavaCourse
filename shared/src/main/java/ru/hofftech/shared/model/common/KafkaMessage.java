package ru.hofftech.shared.model.common;

import java.util.Map;

/**
 * Интерфейс для сообщений Kafka с заголовками.
 * <p>
 * Определяет контракт для объектов, которые могут быть отправлены
 * в Kafka с пользовательскими заголовками.
 */
public interface KafkaMessage {
    /**
     * Возвращает заголовки сообщения.
     *
     * @return карта заголовков, где ключ - имя заголовка, значение - значение заголовка
     */
    Map<String, Object> getHeaders();

    /**
     * Возвращает полезную нагрузку сообщения.
     *
     * @return объект с данными, который будет передан в Kafka
     */
    Object getPayload();
}
