package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;

/**
 * DTO для передачи данных о размещённой в машине посылке.
 * <p>
 * Содержит информацию о посылке и её координатах внутри машины.
 *
 * @param parcel DTO посылки
 * @param x      координата X левого нижнего угла посылки в машине
 * @param y      координата Y левого нижнего угла посылки в машине
 */
@NullMarked
@Builder
public record PlacedParcelDto(ParcelDto parcel, int x, int y) {}
