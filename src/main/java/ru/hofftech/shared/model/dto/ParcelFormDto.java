package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NonNull;

/**
 * DTO для создания/обновления посылки.
 * Содержит все необходимые данные для создания новой посылки.
 *
 * @param name   название посылки (не может быть null)
 * @param form   строковое представление формы посылки (не может быть null)
 * @param symbol символ посылки (не может быть null)
 */
@Builder
public record ParcelFormDto(@NonNull String name, @NonNull String form, @NonNull String symbol) {}
