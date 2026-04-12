package ru.hofftech.billing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import ru.hofftech.billing.exception.BillingException;
import ru.hofftech.billing.mapper.BillingMapper;
import ru.hofftech.billing.model.entity.BillingEntity;
import ru.hofftech.billing.repository.BillingRepository;
import ru.hofftech.billing.util.KafkaHeadersUtil;
import ru.hofftech.billing.util.PageDtoUtil;
import ru.hofftech.shared.model.dto.BillingDto;
import ru.hofftech.shared.model.dto.KafkaHeadersDto;
import ru.hofftech.shared.model.dto.PageDto;
import ru.hofftech.shared.model.enums.BillingOperationType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Сервис для работы с биллингом.
 * <p>
 * Предоставляет методы для создания записей о платежах,
 * получения истории биллинга пользователей и обработки
 * входящих сообщений из Kafka.
 */
@Service
@RequiredArgsConstructor
@NullMarked
@Slf4j
public class BillingService {
    private final BillingRepository billingRepository;
    private final BillingMapper billingMapper;
    private final CacheManager cacheManager;

    /**
     * Обрабатывает входящее сообщение из Kafka.
     * <p>
     * Извлекает заголовки и тело сообщения, создаёт запись в биллинге.
     * Внешний идентификатор (messageId из заголовков) используется
     * для дедупликации сообщений (Transactional Inbox).
     *
     * @param body    тело сообщения (BillingDto)
     * @param headers заголовки сообщения
     * @throws BillingException если запись с таким externalId уже существует
     */
    public void listener(BillingDto body, MessageHeaders headers) {
        KafkaHeadersDto kafkaHeaders = KafkaHeadersUtil.mapToHeaderDto(headers);
        String key = KafkaHeadersUtil.mapToKey(headers);
        log.info("Получено сообщение: {} {} {}", key, kafkaHeaders, body);

        createBilling(
                kafkaHeaders.messageId(),
                body.userId(),
                body.operationType(),
                body.machineCount(),
                body.parcelCount(),
                body.totalAmount());

        log.info("Обработано сообщение: {} {} {}", key, kafkaHeaders, body);
    }

    /**
     * Получает историю биллинга для пользователя за период с пагинацией.
     *
     * @param userId идентификатор пользователя
     * @param from   дата начала периода (опционально)
     * @param to     дата окончания периода (опционально)
     * @param page   номер страницы (начиная с 0)
     * @param size   размер страницы
     * @return пагинированный список записей биллинга
     */
    @Cacheable(value = "billingHistory", key = "#userId + '_' + #from + '_' + #to + '_' + #page + '_' + #size")
    public PageDto<BillingDto> requestBillingHistory(
            String userId, @Nullable LocalDate from, @Nullable LocalDate to, int page, int size) {

        log.info(
                "Запрос истории биллинга для пользователя {}: период {} - {}, страница {}/{}",
                userId,
                from,
                to,
                page,
                size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDt"));
        Page<BillingEntity> billingPage;

        if (from == null && to == null) {
            billingPage = billingRepository.findByUserIdOrderByCreatedDtDesc(userId, pageable);
        } else if (from == null) {
            billingPage = billingRepository.findByUserIdAndCreatedDtBeforeOrderByCreatedDtDesc(
                    userId, to.atTime(23, 59, 59), pageable);
        } else if (to == null) {
            billingPage = billingRepository.findByUserIdAndCreatedDtAfterOrderByCreatedDtDesc(
                    userId, from.atStartOfDay(), pageable);
        } else {
            billingPage = billingRepository.findByUserIdAndCreatedDtBetweenOrderByCreatedDtDesc(
                    userId, from.atStartOfDay(), to.atTime(23, 59, 59), pageable);
        }

        Page<BillingDto> dtoPage = billingPage.map(billingMapper::billingEntityToBillingDto);
        return PageDtoUtil.from(dtoPage);
    }

    /**
     * Создаёт запись о биллинге для операции.
     *
     * @param externalId    внешний идентификатор (для дедупликации)
     * @param userId        идентификатор пользователя
     * @param operationType тип операции (LOAD/UNLOAD)
     * @param machineCount  количество использованных машин
     * @param parcelCount   количество обработанных посылок
     * @param totalAmount   сумма операции
     * @throws BillingException если запись с таким externalId уже существует
     */
    private void createBilling(
            String externalId,
            String userId,
            BillingOperationType operationType,
            Integer machineCount,
            Integer parcelCount,
            BigDecimal totalAmount) {

        if (billingRepository.existsByExternalId(externalId)) {
            throw new BillingException("Запись биллинга для внешнего ID %s уже существует".formatted(externalId));
        }

        BillingEntity entity = BillingEntity.builder()
                .externalId(externalId)
                .userId(userId)
                .operationType(operationType)
                .machineCount(machineCount)
                .parcelCount(parcelCount)
                .totalAmount(totalAmount)
                .build();

        billingRepository.save(entity);
        log.info(
                "Создана запись биллинга для операции {}: userId={}, amount={}, machines={}, parcels={}",
                operationType,
                userId,
                totalAmount,
                machineCount,
                parcelCount);

        evictBillingHistoryCache(userId);
    }

    /**
     * Инвалидирует кеш истории биллинга для пользователя.
     *
     * @param userId идентификатор пользователя
     */
    public void evictBillingHistoryCache(String userId) {
        Cache cache = cacheManager.getCache("billingHistory");
        if (cache instanceof CaffeineCache caffeineCache) {
            var nativeCache = caffeineCache.getNativeCache();
            int removedCount = 0;

            for (Object key : nativeCache.asMap().keySet()) {
                if (key.toString().startsWith(userId)) {
                    nativeCache.invalidate(key);
                    removedCount++;
                }
            }
            log.info("Кеш истории биллинга для userId={} очищен. Удалено записей: {}", userId, removedCount);
        } else if (cache != null) {
            log.error("Кеш истории биллинга billingHistory не поддерживается и не очищен");
        }
    }
}
