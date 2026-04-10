package ru.hofftech.shared.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.jspecify.annotations.NullMarked;

/**
 * DTO для передачи названия посылки.
 *
 * @param name название посылки
 */
@NullMarked
@Builder
public record ParcelNameRequestDto(@NotBlank(message = "Название посылки не может быть пустым") String name) {}
