package ru.hofftech.shared.model.dto;

import lombok.Builder;
import lombok.NonNull;

import java.util.List;

/**
 * DTO для передачи данных о машине.
 * Содержит список размещённых посылок и размеры машины.
 */
@Builder
public record MachineDto(@NonNull List<PlacedParcelDto> parcels, int width, int height) {}
