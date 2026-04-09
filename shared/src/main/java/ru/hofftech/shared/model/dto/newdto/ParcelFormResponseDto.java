package ru.hofftech.shared.model.dto.newdto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;

/**
 * DTO для ответа при создании/обновлении посылки.
 *
 * @param name   название посылки
 * @param form   строковое представление формы посылки
 * @param symbol символ посылки
 */
@NullMarked
@Builder
public record ParcelFormResponseDto(String name, String form, Character symbol) {}
