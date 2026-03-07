package ru.hofftech.importmachine.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import ru.hofftech.shared.model.dto.ParcelDto;

import java.util.List;

@Builder
public record ImportMachineOutputResultDto(@NonNull List<ParcelDto> parcels // Успешно распакованные посылки
        ) {}
