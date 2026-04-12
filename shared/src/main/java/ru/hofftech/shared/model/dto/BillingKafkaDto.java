package ru.hofftech.shared.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.hofftech.shared.model.common.KafkaMessage;
import ru.hofftech.shared.util.KafkaMessageUtil;

import java.util.Map;

/**
 * DTO для отправки информации о биллинге в Kafka.
 * <p>
 * Оборачивает тело сообщения (BillingDto) и заголовки (KafkaHeadersDto)
 * в формат, совместимый с KafkaMessage.
 *
 * @param headers заголовки сообщения
 * @param body    тело сообщения с данными биллинга
 */
@Builder
public record BillingKafkaDto(@NotNull KafkaHeadersDto headers, @NotNull BillingDto body) implements KafkaMessage {

    /**
     * {@inheritDoc}
     * <p>
     * Преобразует DTO заголовков в карту для Kafka.
     *
     * @return карта заголовков
     */
    @Override
    public Map<String, Object> getHeaders() {
        return KafkaMessageUtil.headerDtoToMap(headers);
    }

    /**
     * {@inheritDoc}
     *
     * @return тело сообщения (BillingDto)
     */
    @Override
    public Object getPayload() {
        return body;
    }
}
