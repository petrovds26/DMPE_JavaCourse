package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;

/**
 * DTO координаты клетки в сетке посылки или машины.
 * <p>
 * Используется для передачи информации о положении заполненной клетки
 * в двумерном представлении посылки или машины.
 *
 * @param x координата по горизонтали (от 0 слева направо)
 * @param y координата по вертикали (от 0 снизу вверх)
 */
@NullMarked
@Builder
public record CoordinateDto(int x, int y) {}
