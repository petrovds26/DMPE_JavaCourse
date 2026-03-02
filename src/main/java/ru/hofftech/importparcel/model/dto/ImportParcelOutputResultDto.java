package ru.hofftech.importparcel.model.dto;

import lombok.Builder;
import ru.hofftech.shared.model.dto.MachineDto;

import java.util.List;

@Builder
public record ImportParcelOutputResultDto(
        List<ImportParcelInvalidDto>
                invalidParcels, // Посылки, которые содержат ошибки (на этапе валидации, по габаритам, потому что мало
        // машин и т.д.)
        List<MachineDto> machines, // Успешно упакованные машины
        List<String> errors // Прочие ошибки обработки
        ) {}
