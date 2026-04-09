package ru.hofftech.shared.model.dto.newdto;

import lombok.Builder;
import lombok.With;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.dto.MachineDto;

import java.util.List;

/**
 * DTO ответа на запрос загрузки посылок в машины.
 *
 * @param statistic статистика загрузки
 * @param machines  список машин с размещёнными посылками
 */
@Builder
@NullMarked
@With
public record LoadResponseDto(LoadStatisticDto statistic, @Nullable List<MachineDto> machines) {}
