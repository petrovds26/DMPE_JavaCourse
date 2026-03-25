package ru.hofftech.load.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import ru.hofftech.shared.model.dto.MachineDto;

import java.util.List;

@Builder
@NullMarked
public record LoadParcelOutputResultDto(
        @Nullable
                List<LoadParcelInvalidDto>
                        invalidParcels, // Посылки, которые содержат ошибки (на этапе валидации, по габаритам, потому
        // что мало
        // машин и т.д.)
        @Nullable List<MachineDto> machines, // Успешно упакованные машины
        @Nullable List<String> errors // Прочие ошибки обработки
        ) {}
