package ru.hofftech.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.hofftech.core.mapper.CoreMapper;
import ru.hofftech.core.model.entity.BillingEntity;
import ru.hofftech.core.repository.BillingRepository;
import ru.hofftech.core.util.PageDtoUtil;
import ru.hofftech.shared.model.dto.BillingDto;
import ru.hofftech.shared.model.dto.PageDto;
import ru.hofftech.shared.model.enums.BillingOperationType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Сервис для работы с биллингом.
 * <p>
 * Предоставляет методы для создания записей о платежах
 * и получения истории биллинга пользователей.
 */
@Service
@RequiredArgsConstructor
@NullMarked
@Slf4j
public class BillingService {
    private final BillingRepository billingRepository;
    private final CoreMapper coreMapper;

    /**
     * Создаёт запись о биллинге для операции.
     *
     * @param userId        идентификатор пользователя
     * @param operationType тип операции (LOAD/UNLOAD)
     * @param machineCount  количество использованных машин
     * @param parcelCount   количество обработанных посылок
     * @param totalAmount   сумма операции
     */
    public void createBilling(
            String userId,
            BillingOperationType operationType,
            Integer machineCount,
            Integer parcelCount,
            BigDecimal totalAmount) {
        BillingEntity entity = BillingEntity.builder()
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
    public PageDto<BillingDto> requestBillingHistory(
            String userId, @Nullable LocalDate from, @Nullable LocalDate to, int page, int size) {

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

        Page<BillingDto> dtoPage = billingPage.map(coreMapper::billingEntityToBillingDto);
        return PageDtoUtil.from(dtoPage);
    }
}
