package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * DTO для передачи данных о посылке между сервисами.
 * <p>
 * Содержит полную информацию о посылке:
 * название, форму, символ заполнения, размеры и координаты заполненных клеток.
 *
 * @param name        название посылки
 * @param coordinates список координат заполненных клеток (для точного восстановления формы)
 * @param form        строковое представление формы посылки
 * @param symbol      символ, которым заполнена посылка
 * @param height      высота посылки в клетках
 * @param width       ширина посылки в клетках
 */
@NullMarked
@Builder
public record ParcelDto(
        String name, List<CoordinateDto> coordinates, String form, Character symbol, Integer height, Integer width) {}
