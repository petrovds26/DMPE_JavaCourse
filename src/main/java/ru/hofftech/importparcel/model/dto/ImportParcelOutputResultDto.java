package ru.hofftech.importparcel.model.dto;

import lombok.Builder;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.dto.MachineDto;

import java.util.List;

@Builder
public record ImportParcelOutputResultDto(
        @Nullable
                List<ImportParcelInvalidDto>
                        invalidParcels, // Посылки, которые содержат ошибки (на этапе валидации, по габаритам, потому
        // что мало
        // машин и т.д.)
        @Nullable List<MachineDto> machines, // Успешно упакованные машины
        @Nullable List<String> errors // Прочие ошибки обработки
        ) {}
