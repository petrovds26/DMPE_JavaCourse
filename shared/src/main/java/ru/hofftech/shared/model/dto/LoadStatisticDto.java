package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO статистики загрузки посылок.
 *
 * @param errors                     список общих ошибок
 * @param invalidParcels             список проблемных посылок
 * @param totalInputParcels          общее количество посылок на входе
 * @param totalSuccessLoadParcels    количество успешно загруженных посылок
 * @param totalSegments              общее количество загруженных сегментов (заполненных клеток)
 * @param totalUsedMachines          количество использованных машин
 * @param priceSegment               цена за один сегмент
 * @param totalAmount                общая стоимость загрузки
 */
@Builder
@NullMarked
public record LoadStatisticDto(
        @Nullable List<String> errors,
        @Nullable List<LoadParcelInvalidDto> invalidParcels,
        Integer totalInputParcels,
        Integer totalSuccessLoadParcels,
        Integer totalSegments,
        Integer totalUsedMachines,
        BigDecimal priceSegment,
        BigDecimal totalAmount) {}
