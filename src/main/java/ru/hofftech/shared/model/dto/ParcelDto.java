package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * DTO для передачи данных о посылке.
 * Содержит строковое представление формы посылки и координаты.
 *
 * @param name название посылки (не может быть null)
 * @param coordinates координаты заполненных клеток (может быть null)
 * @param form строковое представление формы (может быть null)
 *
 */
@Builder
public record ParcelDto(@NonNull String name, @Nullable List<CoordinateDto> coordinates, @Nullable String form) {}
