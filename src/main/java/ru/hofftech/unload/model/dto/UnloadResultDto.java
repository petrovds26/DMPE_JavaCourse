package ru.hofftech.unload.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import ru.hofftech.shared.model.dto.ParcelDto;

import java.util.List;

@Builder
@NullMarked
public record UnloadResultDto(List<ParcelDto> parcels // Успешно распакованные посылки
        ) {}
