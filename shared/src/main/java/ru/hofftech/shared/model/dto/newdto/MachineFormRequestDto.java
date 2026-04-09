package ru.hofftech.shared.model.dto.newdto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.jspecify.annotations.NullMarked;

/**
 * DTO для создания машины по строковому представлению формы.
 *
 * @param form строковое представление формы машины (например, "6x6")
 */
@NullMarked
@Builder
public record MachineFormRequestDto(@NotBlank(message = "Форма машины не может быть пустой") String form) {}
