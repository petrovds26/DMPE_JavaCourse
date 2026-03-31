package ru.hofftech.shared.model.dto;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;

/**
 * DTO для передачи данных о размещённой в машине посылке.
 * Содержит DTO посылки и её координаты.
 */
@NullMarked
@Builder
public record PlacedParcelDto(ParcelDto parcel, int x, int y) {}
