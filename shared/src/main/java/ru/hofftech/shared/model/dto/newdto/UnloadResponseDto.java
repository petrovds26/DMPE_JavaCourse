package ru.hofftech.shared.model.dto.newdto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.dto.ParcelDto;

import java.util.List;

/**
 * DTO ответа на запрос разгрузки машин.
 *
 * @param statistic статистика разгрузки
 * @param parcels   список разгруженных посылок
 */
@Builder
@NullMarked
public record UnloadResponseDto(UnloadStatisticDto statistic, @Nullable List<ParcelDto> parcels) {}
