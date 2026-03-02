package ru.hofftech.shared.model.dto;

import lombok.Builder;
import lombok.NonNull;

/**
 * DTO для передачи данных о посылке.
 * Содержит строковое представление формы посылки.
 */
@Builder
public record ParcelDto(@NonNull String form) {}
