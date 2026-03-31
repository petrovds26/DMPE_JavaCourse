package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * DTO для передачи данных о машине.
 * Содержит список размещённых посылок и размеры машины.
 */
@NullMarked
@Builder
public record MachineDto(List<PlacedParcelDto> parcels, int width, int height) {}
