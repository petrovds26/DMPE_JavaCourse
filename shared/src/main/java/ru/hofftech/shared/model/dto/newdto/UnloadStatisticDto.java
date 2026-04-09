package ru.hofftech.shared.model.dto.newdto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;

/**
 * DTO статистики разгрузки машин.
 *
 * @param totalUnloadMachines       количество разгруженных машин
 * @param totalSuccessUnloadParcels количество успешно разгруженных посылок
 * @param totalSegments             общее количество разгруженных сегментов
 * @param priceSegment              цена за один сегмент
 * @param totalAmount               общая стоимость разгрузки
 */
@Builder
@NullMarked
public record UnloadStatisticDto(
        Integer totalUnloadMachines,
        Integer totalSuccessUnloadParcels,
        Integer totalSegments,
        BigDecimal priceSegment,
        BigDecimal totalAmount) {}
