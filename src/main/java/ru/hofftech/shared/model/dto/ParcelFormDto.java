package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NonNull;

/**
 * DTO для создания/обновления посылки.
 *
 * @param name название посылки
 * @param form строковое представление формы
 * @param symbol символ посылки
 */
@Builder
public record ParcelFormDto(@NonNull String name, @NonNull String form, @NonNull String symbol) {}
