package ru.hofftech.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import ru.hofftech.shared.model.common.KafkaMessage;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис для отправки сообщений в Kafka.
 * <p>
 * Предоставляет методы для публикации событий и отправки сообщений в DLQ.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {
    private final Map<String, String> topics;
    private final StreamBridge streamBridge;
    private static final String X_ORIGINAL_KEY = "x-original-key";

    /**
     * Отправляет сообщение в указанный топик.
     *
     * @param eventType тип события для определения топика
     * @param key       ключ сообщения
     * @param payload   данные сообщения
     */
    public void sendMessage(String eventType, Object key, Object payload) {
        publish(eventType, key, payload);
    }

    /**
     * Отправляет сообщение в DLQ с сохранением оригинальных заголовков.
     *
     * @param message         сообщение для отправки
     * @param originalHeaders оригинальные заголовки
     * @param exception       исключение, вызвавшее отправку в DLQ
     * @param dlqTopic        топик DLQ
     */
    public void sendToDlq(Object message, MessageHeaders originalHeaders, Exception exception, String dlqTopic) {
        try {
            Map<String, Object> dlqHeaders = new HashMap<>();

            copyHeader(originalHeaders, KafkaHeaders.RECEIVED_TOPIC, "x-original-topic", dlqHeaders);
            copyHeader(originalHeaders, "kafka_receivedPartitionId", "x-original-partition", dlqHeaders);
            copyHeader(originalHeaders, KafkaHeaders.OFFSET, "x-original-offset", dlqHeaders);
            copyHeader(originalHeaders, "kafka_receivedMessageKey", X_ORIGINAL_KEY, dlqHeaders);
            copyHeader(originalHeaders, "kafka_receivedTimestamp", "x-original-timestamp", dlqHeaders);
            copyHeader(originalHeaders, "kafka_timestampType", "x-original-timestamp-type", dlqHeaders);

            if (exception != null) {
                dlqHeaders.put("x-exception-message", exception.getMessage());
                dlqHeaders.put("x-exception-fqcn", exception.getClass().getName());
                dlqHeaders.put("x-exception-stacktrace", getStackTrace(exception));
            } else {
                dlqHeaders.put("x-exception-message", "validation-failed");
                dlqHeaders.put("x-exception-fqcn", "UnknownException");
                dlqHeaders.put("x-exception-stacktrace", "No stacktrace available");
            }

            Message<Object> dlqMessage = createDlqMessage(message, dlqHeaders);

            streamBridge.send(dlqTopic, dlqMessage);
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения в DLQ: {}", e.getMessage(), e);
        }
    }

    /**
     * Копирует заголовок из оригинального сообщения в DLQ заголовки.
     *
     * @param originalHeaders оригинальные заголовки
     * @param originalKey     ключ оригинального заголовка
     * @param dlqKey          ключ в DLQ
     * @param dlqHeaders      карта DLQ заголовков
     */
    private void copyHeader(
            MessageHeaders originalHeaders, String originalKey, String dlqKey, Map<String, Object> dlqHeaders) {
        if (originalHeaders.containsKey(originalKey)) {
            Object value = originalHeaders.get(originalKey);
            if (value instanceof List<?> list && !list.isEmpty()) {
                dlqHeaders.put(dlqKey, list.getFirst());
            } else {
                dlqHeaders.put(dlqKey, value);
            }
        }
    }

    /**
     * Получает stacktrace из исключения.
     *
     * @param exception исключение
     * @return строковое представление stacktrace
     */
    private String getStackTrace(Exception exception) {
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : exception.getStackTrace()) {
            stackTrace.append(element.toString()).append("\n");
        }
        return stackTrace.toString();
    }

    /**
     * Создаёт DLQ сообщение с заголовками.
     *
     * @param message    тело сообщения
     * @param dlqHeaders заголовки DLQ
     * @return сообщение для отправки в DLQ
     */
    private Message<Object> createDlqMessage(Object message, Map<String, Object> dlqHeaders) {
        MessageBuilder<Object> messageBuilder = MessageBuilder.withPayload(message);

        Object keyValue = dlqHeaders.get(X_ORIGINAL_KEY);
        if (keyValue != null) {
            if (keyValue instanceof byte[]) {
                messageBuilder.setHeader(KafkaHeaders.KEY, keyValue);
            } else {
                messageBuilder.setHeader(KafkaHeaders.KEY, keyValue.toString().getBytes(StandardCharsets.UTF_8));
            }
        }

        dlqHeaders.forEach((key, value) -> {
            if (!X_ORIGINAL_KEY.equals(key)) {
                messageBuilder.setHeader(key, value);
            }
        });

        messageBuilder.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);

        return messageBuilder.build();
    }

    /**
     * Публикует сообщение в Kafka.
     *
     * @param eventType     тип события
     * @param key           ключ сообщения
     * @param messageObject объект сообщения
     */
    private void publish(String eventType, Object key, Object messageObject) {
        String topic = resolveTopic(eventType);
        Map<String, Object> kafkaHeaders = new HashMap<>();
        Object payload;

        if (messageObject instanceof KafkaMessage kafkaMessage) {
            kafkaHeaders = kafkaMessage.getHeaders();
            payload = kafkaMessage.getPayload();
        } else {
            payload = messageObject;
        }

        MessageBuilder<Object> messageBuilder = MessageBuilder.withPayload(payload);

        if (key != null) {
            messageBuilder.setHeader(KafkaHeaders.KEY, key.toString().getBytes(StandardCharsets.UTF_8));
        }

        kafkaHeaders.forEach((keyElem, value) -> {
            if (value instanceof String strValue) {
                messageBuilder.setHeader(keyElem, strValue);
            } else {
                messageBuilder.setHeader(keyElem, value);
            }
        });
        messageBuilder.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);

        Message<Object> message = messageBuilder.build();

        streamBridge.send(topic, message);
    }

    /**
     * Определяет топик по типу события.
     *
     * @param eventType тип события
     * @return имя топика Kafka
     */
    private String resolveTopic(String eventType) {
        return topics.get(eventType + "-out-0");
    }
}
