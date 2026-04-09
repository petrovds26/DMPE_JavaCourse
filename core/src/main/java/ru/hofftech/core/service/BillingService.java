package ru.hofftech.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.hofftech.core.mapper.CoreMapper;
import ru.hofftech.core.model.entity.BillingEntity;
import ru.hofftech.core.repository.BillingRepository;
import ru.hofftech.shared.model.dto.newdto.BillingDto;
import ru.hofftech.shared.model.enums.BillingOperationType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
     * Получает историю биллинга для пользователя за указанный период.
     *
     * @param userId идентификатор пользователя
     * @param from   дата начала периода (опционально)
     * @param to     дата окончания периода (опционально)
     * @return список записей биллинга
     */
    public List<BillingDto> requestBillingHistory(String userId, @Nullable LocalDate from, @Nullable LocalDate to) {

        if (from == null && to == null) {
            return coreMapper.billingEntityListToBillingDtoList(
                    billingRepository.findByUserIdOrderByCreatedDtDesc(userId));
        } else if (from == null) {
            return coreMapper.billingEntityListToBillingDtoList(
                    billingRepository.findByUserIdAndCreatedDtBeforeOrderByCreatedDtDesc(
                            userId, to.atTime(23, 59, 59)));

        } else if (to == null) {
            return coreMapper.billingEntityListToBillingDtoList(
                    billingRepository.findByUserIdAndCreatedDtAfterOrderByCreatedDtDesc(userId, from.atStartOfDay()));
        }

        return coreMapper.billingEntityListToBillingDtoList(
                billingRepository.findByUserIdAndCreatedDtBetweenOrderByCreatedDtDesc(
                        userId, from.atStartOfDay(), to.atTime(23, 59, 59)));
    }
}
