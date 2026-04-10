package ru.hofftech.shared.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.jspecify.annotations.NullMarked;

/**
 * DTO для создания/обновления посылки.
 *
 * @param name   название посылки
 * @param form   строковое представление формы посылки
 * @param symbol символ посылки (один символ)
 */
@NullMarked
@Builder
public record ParcelFormRequestDto(
        @NotBlank(message = "Название посылки не может быть пустым") String name,
        @NotBlank(message = "Форма посылки не может быть пустой") String form,
        @NotBlank(message = "Символ посылки не может быть пустым")
                @Size(min = 1, max = 1, message = "Символ посылки должен быть из одного символа.")
                String symbol) {}
