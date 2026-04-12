package ru.hofftech.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hofftech.core.model.entity.BillingOutboxEntity;
import ru.hofftech.core.repository.BillingOutboxRepository;
import ru.hofftech.core.util.JsonUtil;
import ru.hofftech.shared.model.dto.BillingDto;
import ru.hofftech.shared.model.dto.BillingKafkaDto;
import ru.hofftech.shared.model.dto.KafkaHeadersDto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Сервис для работы с outbox таблицей биллинга.
 * <p>
 * Обеспечивает сохранение событий в outbox таблицу в рамках транзакции
 * и их последующую отправку в Kafka через планировщик.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@NullMarked
public class BillingOutboxService {

    private final BillingOutboxRepository outboxRepository;
    private final KafkaService kafkaService;

    /**
     * Сохраняет событие в outbox таблицу в рамках текущей транзакции.
     *
     * @param billingDto DTO с данными для биллинга
     */
    @Transactional
    public void saveEvent(BillingDto billingDto) {
        String payloadJson = JsonUtil.toJson(billingDto);

        BillingOutboxEntity entity =
                BillingOutboxEntity.builder().payload(payloadJson).build();

        outboxRepository.save(entity);
        log.info("Событие сохранено в outbox: id={}", entity.getOutboxKey());
    }

    /**
     * Обрабатывает неотправленные события из outbox таблицы.
     * <p>
     * Для каждого неотправленного события:
     * <ul>
     *   <li>Формирует Kafka сообщение с заголовками</li>
     *   <li>Отправляет сообщение в Kafka</li>
     *   <li>Отмечает событие как отправленное</li>
     * </ul>
     */
    public void handle() {
        List<BillingOutboxEntity> billingOutboxEntity = outboxRepository.findBySentDtIsNullOrderByCreatedDtAsc();
        for (BillingOutboxEntity entity : billingOutboxEntity) {
            BillingDto body = JsonUtil.fromJson(entity.getPayload(), new TypeReference<>() {});
            KafkaHeadersDto headers = KafkaHeadersDto.builder()
                    .source("core")
                    .target("billing")
                    .created(LocalDateTime.now(ZoneOffset.UTC).toString())
                    .type("JSON")
                    .payloadVersion("1.0.0")
                    .messageId(entity.getOutboxKey().toString())
                    .eventType("BILLING_CREATE")
                    .build();
            BillingKafkaDto billingKafkaDto =
                    BillingKafkaDto.builder().headers(headers).body(body).build();

            kafkaService.sendMessage("billingOutbox", entity.getOutboxKey().toString(), billingKafkaDto);

            entity.setSentDt(LocalDateTime.now(ZoneOffset.UTC));
            outboxRepository.save(entity);

            log.info("Сообщение {} было отправлено", entity.getOutboxKey());
        }
    }
}
