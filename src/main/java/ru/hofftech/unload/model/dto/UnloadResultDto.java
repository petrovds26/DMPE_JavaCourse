package ru.hofftech.unload.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.dto.ParcelDto;

import java.util.List;

@Builder
public record UnloadResultDto(@NonNull List<ParcelDto> parcels // Успешно распакованные посылки
        ) {}
