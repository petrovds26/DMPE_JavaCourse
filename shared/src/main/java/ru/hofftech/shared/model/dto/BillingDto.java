package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO записи биллинга для передачи между сервисами.
 * <p>
 * Содержит информацию о платеже за операцию погрузки или разгрузки.
 *
 * @param userId        идентификатор пользователя
 * @param operationType тип операции (LOAD/UNLOAD)
 * @param machineCount  количество использованных машин
 * @param parcelCount   количество обработанных посылок
 * @param totalAmount   сумма операции в рублях
 * @param createdDt     дата и время создания записи
 */
@NullMarked
@Builder
public record BillingDto(
        String userId,
        String operationType,
        Integer machineCount,
        Integer parcelCount,
        BigDecimal totalAmount,
        LocalDateTime createdDt) {}
