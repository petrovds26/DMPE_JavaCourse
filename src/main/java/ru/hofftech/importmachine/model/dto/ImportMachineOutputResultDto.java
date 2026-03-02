package ru.hofftech.importmachine.model.dto;

import lombok.Builder;
import ru.hofftech.shared.model.dto.ParcelDto;

import java.util.List;

@Builder
public record ImportMachineOutputResultDto(List<ParcelDto> parcels // Успешно распакованные посылки
        ) {}
